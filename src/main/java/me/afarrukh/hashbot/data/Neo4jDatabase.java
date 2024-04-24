package me.afarrukh.hashbot.data;

import com.google.inject.Inject;
import me.afarrukh.hashbot.commands.audiotracks.playlist.TrackData;
import me.afarrukh.hashbot.config.Config;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.exceptions.PlaylistException;
import me.afarrukh.hashbot.track.Playlist;
import me.afarrukh.hashbot.track.PlaylistItem;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Runtime.getRuntime;
import static java.util.Collections.singletonMap;

public class Neo4jDatabase implements Database {
    private static final Logger LOG = LoggerFactory.getLogger(Neo4jDatabase.class);
    private static Neo4jDatabase instance;
    private final Driver driver;
    private final Config config;
    private final Map<String, String> guildPrefixes;

    @Inject
    public Neo4jDatabase(Config config) {
        driver = GraphDatabase.driver(
                config.getDbUri(), AuthTokens.basic(config.getDbUsername(), config.getDbPassword()));
        this.config = config;
        guildPrefixes = new HashMap<>();
        var run = driver.session().run("MATCH (n) RETURN COUNT(n) AS count");
        var count = run.single().get("count").asInt();
        LOG.info("Connected to neo4j database with {} nodes", count);
    }

    @Override
    public String getPrefixForGuild(String guildId) {
        if (guildPrefixes.get(guildId) != null) {
            return guildPrefixes.get(guildId);
        }
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
            var prefix = result.asString();
            guildPrefixes.put(guildId, prefix);
            return prefix;
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
    public boolean deletePlaylistForUser(String playlistName, String userId) {
        var initialRun = driver.session()
                .run(
                        """
                MATCH (u:User)-[:HAS_PLAYLIST]->(p:Playlist)
                WHERE u.id = $user_id AND p.name = $playlist_name
                DETACH DELETE p""",
                        Map.of("user_id", userId, "playlist_name", playlistName));
        driver.session().run("MATCH (t:Track) WHERE NOT (:Playlist)-[:HAS_TRACK]->(t) DETACH DELETE t");
        return initialRun.consume().counters().nodesDeleted() > 0;
    }

    @Override
    public List<Playlist> getAllPlaylistsForUser(String userId) {
        Map<String, Object> parameters = Map.of("user_id", userId);
        var run = driver.session()
                .run(
                        "MATCH (u:User)-[:HAS_PLAYLIST]->(p:Playlist) WHERE u.id = $user_id RETURN p.name AS playlistName",
                        parameters);
        return run.list().parallelStream()
                .map(r -> getPlaylistForUser(r.get("playlistName").asString(), userId))
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public void setPinnedChannelForGuild(String guildId, String channelId) {
        Map<String, Object> parameters = Map.of("guild_id", guildId, "channel_id", channelId);
        driver.session().run("MERGE (g:Guild {id: $guild_id}) SET g.pinnedChannel = $channel_id", parameters);
    }

    @Override
    public void setPinThresholdForGuild(String guildId, int threshold) {
        Map<String, Object> parameters = Map.of("guild_id", guildId, "threshold", threshold);
        driver.session().run("MERGE (g:Guild {id: $guild_id}) SET g.pinThreshold = $threshold", parameters);
    }

    @Override
    public void setPrefixForGuild(String guildId, String prefix) {
        Map<String, Object> parameters = Map.of("guild_id", guildId, "prefix", prefix);
        driver.session().run("MERGE (g:Guild {id: $guild_id}) SET g.prefix = $prefix", parameters);
        guildPrefixes.put(guildId, prefix);
    }

    @Override
    public void unsetPinnedChannelForGuild(String guildId) {
        Map<String, Object> parameters = Map.of("guild_id", guildId);
        driver.session().run("MERGE (g:Guild {id: $guild_id}) REMOVE g.pinnedChannel", parameters);
    }

    @Override
    public boolean isMessagePinnedInGuild(String guildId, String originalMessageId) {
        Map<String, Object> parameters = Map.of("guild_id", guildId, "message_id", originalMessageId);
        var result = driver.session()
                .run(
                        """
                                MATCH (g:Guild)-[:HAS_PINNED_MESSAGE]->(p:PinnedMessage)
                                WHERE g.id = $guild_id
                                AND p.originalMessageId = $message_id
                                RETURN p""",
                        parameters);
        return result.hasNext();
    }

    @Override
    public void setMessageAsPinnedInGuild(String guildId, String originalMessageId, String newMessageId) {
        Map<String, Object> parameters =
                Map.of("guild_id", guildId, "original_message_id", originalMessageId, "new_message_id", newMessageId);
        driver.session()
                .run(
                        """
                MERGE (g:Guild {id: $guild_id})
                MERGE (p:PinnedMessage {originalMessageId: $original_message_id, newMessageId: $new_message_id})
                MERGE (g)-[:HAS_PINNED_MESSAGE]->(p)""",
                        parameters);
    }

    @Override
    public boolean isBotPinMessageInGuild(String guildId, String messageId) {
        Map<String, Object> parameters = Map.of("guild_id", guildId, "message_id", messageId);
        var result = driver.session()
                .run(
                        """
                                MATCH (g:Guild)-[:HAS_PINNED_MESSAGE]->(p:PinnedMessage)
                                WHERE g.id = $guild_id
                                AND p.newMessageId = $message_id
                                RETURN p""",
                        parameters);
        return result.hasNext();
    }

    @Override
    public Optional<String> getPinnedChannelIdForGuild(String guildId) {
        var result = driver.session()
                .run(
                        "MATCH (g:Guild) WHERE g.id = $guild_id RETURN g.pinnedChannel AS pinnedChannel",
                        singletonMap("guild_id", guildId));
        var value = result.single().get("pinnedChannel");
        if (!value.isNull()) {
            return Optional.of(value.asString());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public int getPinThresholdForGuild(String guildId) {
        var result = driver.session()
                .run(
                        """
                MERGE (g:Guild {id: $guild_id})
                RETURN g.pinThreshold AS pinThreshold""",
                        singletonMap("guild_id", guildId));
        var value = result.single().get("pinThreshold");
        if (value.isNull()) {
            driver.session()
                    .run(
                            "MERGE (g:Guild {id: $guild_id}) SET g.pinThreshold = $threshold",
                            Map.of("guild_id", guildId, "threshold", Constants.PIN_THRESHOLD));
            return Constants.PIN_THRESHOLD;
        } else {
            return value.asInt();
        }
    }

    @Override
    public void deletePinnedMessageEntryByOriginalMessageId(String guildId, String messageId) {
        driver.session()
                .run(
                        """
                        MATCH (g:Guild)-[:HAS_PINNED_MESSAGE]->(p:PinnedMessage)
                        WHERE g.id = $guild_id
                        AND p.originalMessageId = $message_id
                        DETACH DELETE p""",
                        Map.of("guild_id", guildId, "message_id", messageId));
    }

    @Override
    public void deletePinnedMessageEntryByBotPinnedMessageId(String guildId, String messageId) {
        driver.session()
                .run(
                        """
                        MATCH (g:Guild)-[:HAS_PINNED_MESSAGE]->(p:PinnedMessage)
                        WHERE g.id = $guild_id
                        AND p.newMessageId = $message_id
                        DETACH DELETE p""",
                        Map.of("guild_id", guildId, "message_id", messageId));
    }
}
