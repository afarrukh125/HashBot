package me.afarrukh.hashbot.data;

import me.afarrukh.hashbot.commands.audiotracks.playlist.TrackData;
import me.afarrukh.hashbot.config.Config;
import me.afarrukh.hashbot.track.Playlist;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Neo4jDatabase implements Database {

    private final Driver driver;

    public Neo4jDatabase(Config config) {
        driver = GraphDatabase.driver(config.getDbUri(), AuthTokens.basic(config.getDbUsername(), config.getDbPassword()));
    }

    // TODO implement

    @Override
    public String getPrefixForGuild(String guildId) {
        return null;
    }

    @Override
    public Optional<Playlist> getPlaylistForUser(String playlistName, String userId) {
        return Optional.empty();
    }

    @Override
    public void createPlaylistForUser(String userId, String playlistName, Map<String, TrackData> trackDataMap) {

    }

    @Override
    public void deletePlaylistForUser(String playlistName, String userId) {

    }

    @Override
    public List<Playlist> getAllPlaylistsForUser(String userId) {
        return null;
    }

    @Override
    public void setPinnedChannelForGuild(String guildId, String channelId) {

    }

    @Override
    public void setPinThresholdForGuild(String id, int threshold) {

    }

    @Override
    public void setPrefixForGuild(String guildiD, String prefix) {

    }

    @Override
    public void unsetPinnedChannelForGuild(String guildId) {

    }

    @Override
    public void setMessageAsPinnedInGuild(String guildId, String originalMessageId, String newMessageId) {

    }

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
    public void deletePinnedMessageEntryByOriginalMessageId(String guildId, String messageId) {

    }

    @Override
    public void deletePinnedMessageEntryByBotPinnedMessageId(String guildId, String messageId) {

    }
}
