package me.afarrukh.hashbot.data;

import me.afarrukh.hashbot.commands.audiotracks.playlist.TrackData;
import me.afarrukh.hashbot.config.Config;
import me.afarrukh.hashbot.exceptions.PlaylistException;
import me.afarrukh.hashbot.track.Playlist;
import me.afarrukh.hashbot.track.PlaylistItem;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Runtime.getRuntime;

public class Neo4jDatabase implements Database {

    private final Driver driver;
    private final Config config;

    public Neo4jDatabase(Config config) {
        driver = GraphDatabase.driver(
                config.getDbUri(), AuthTokens.basic(config.getDbUsername(), config.getDbPassword()));
        this.config = config;
    }

    // TODO implement

    @Override
    public String getPrefixForGuild(String guildId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("guild_id", guildId);
        var result = driver.session()
                .run("MERGE (g:Guild {id: $guild_id}) RETURN g.prefix AS prefix", parameters)
                .single()
                .get("prefix");

        if (result.isNull()) {
            var prefix = config.getPrefix();
            parameters.put("prefix", prefix);
            driver.session().run("MERGE (g:Guild {id: $guild_id}) SET g.prefix = $prefix", parameters);
            return prefix;
        } else {
            return result.asString();
        }
    }

    @Override
    public Optional<Playlist> getPlaylistForUser(String playlistName, String userId) {
        var result = driver.session()
                .run(
                        """
                MATCH (user:User)-[:HAS_PLAYLIST]->(playlist:Playlist)-[r:HAS_TRACK]->(track:Track)
                WHERE playlist.name = $playlist_name
                AND user.id = $user_id
                RETURN track.url AS url
                ORDER BY r.index""",
                        Map.of("playlist_name", playlistName, "user_id", userId));
        if (result.hasNext()) {
            List<PlaylistItem> items =
                    result.list(record -> new PlaylistItem(record.get("url").asString()));
            var playlist = new Playlist(playlistName, items);
            return Optional.of(playlist);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void createPlaylistForUser(String userId, String playlistName, Collection<TrackData> allTrackData)
            throws PlaylistException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", userId);
        parameters.put("playlist_name", playlistName);
        boolean hasList = driver.session()
                .run(
                        "MATCH(u:User)-[:HAS_PLAYLIST]->(p:Playlist)\n" + "WHERE u.id = $user_id\n"
                                + "AND p.name = $playlist_name\n"
                                + "RETURN p",
                        parameters)
                .hasNext();
        if (hasList) {
            throw new PlaylistException(
                    "Playlist with name %s already exists for user with ID %s".formatted(playlistName, userId));
        } else {
            String playlistId = UUID.randomUUID().toString();
            parameters.put("playlist_id", playlistId);
            driver.session()
                    .run(
                            """
                                    MERGE (u:User {id: $user_id})
                                    MERGE (p:Playlist {name: $playlist_name, id: $playlist_id})
                                    MERGE (u)-[:HAS_PLAYLIST]->(p)""",
                            parameters);
            var counter = 0;
            try (var executor = Executors.newFixedThreadPool(getRuntime().availableProcessors() * 2)) {
                for (var trackData : allTrackData) {
                    counter++;
                    attachTrackToPlaylist(playlistId, counter, executor, trackData);
                }
            }
        }
    }

    private void attachTrackToPlaylist(String id, int counter, ExecutorService executor, TrackData trackData) {
        executor.execute(() -> {
            Map<String, Object> trackParams = Map.of("playlist_id", id, "index", counter, "track_url", trackData.url());
            driver.session()
                    .run(
                            "MATCH (p:Playlist) WHERE p.id = $playlist_id MERGE (t:Track {url: $track_url}) MERGE (p)-[:HAS_TRACK {index: $index}]->(t)",
                            trackParams);
        });
    }

    @Override
    public void deletePlaylistForUser(String playlistName, String userId) {}

    @Override
    public List<Playlist> getAllPlaylistsForUser(String userId) {
        return null;
    }

    @Override
    public void setPinnedChannelForGuild(String guildId, String channelId) {}

    @Override
    public void setPinThresholdForGuild(String id, int threshold) {}

    @Override
    public void setPrefixForGuild(String guildiD, String prefix) {}

    @Override
    public void unsetPinnedChannelForGuild(String guildId) {}

    @Override
    public void setMessageAsPinnedInGuild(String guildId, String originalMessageId, String newMessageId) {}

    @Override
    public boolean isBotPinMessageInGuild(String guildId, String messageId) {
        return false;
    }

    @Override
    public Optional<String> getPinnedChannelIdForGuild(String id) {
        return Optional.empty();
    }

    @Override
    public int getPinThresholdForGuild(String id) {
        return 0;
    }

    @Override
    public void deletePinnedMessageEntryByOriginalMessageId(String guildId, String messageId) {}

    @Override
    public void deletePinnedMessageEntryByBotPinnedMessageId(String guildId, String messageId) {}
}
