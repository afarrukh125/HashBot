package me.afarrukh.hashbot.data;

import me.afarrukh.hashbot.commands.audiotracks.playlist.TrackData;
import me.afarrukh.hashbot.config.Config;
import me.afarrukh.hashbot.exceptions.PlaylistException;
import me.afarrukh.hashbot.track.Playlist;
import me.afarrukh.hashbot.track.PlaylistItem;

import java.sql.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;

public class SQLiteDatabase implements Database {

    private static final String PATH = "HashBot.sqlite";
    private final Config config;
    private Connection connection;


    SQLiteDatabase(Config config) {
        try {
            this.config = config;
            this.connection = DriverManager.getConnection("jdbc:sqlite:%s".formatted(PATH));
            createTablesIfNotPresent();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTablesIfNotPresent() {
        try {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS LISTTRACK(listid INTEGER, trackurl VARCHAR(100), position INTEGER)");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS LISTUSER(listid INTEGER CONSTRAINT listuser_pk PRIMARY KEY AUTOINCREMENT, userid VARCHAR(60))");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS PLAYLIST(listid INTEGER CONSTRAINT playlist_pk PRIMARY KEY AUTOINCREMENT, name VARCHAR(60), userid VARCHAR(60))");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS GUILD(id VARCHAR(30), prefix VARCHAR(10))");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Playlist> getPlaylistForUser(String playlistName, String userId) {
        return Optional.empty();
    }

    @Override
    public boolean deletePlaylistForUser(String playlistName, String userId) {
        return false;
    }

    @Override
    public String getPrefixForGuild(String guildId) {
        try {
            var query = "SELECT prefix FROM GUILD WHERE GUILD.id = %s".formatted(guildId);
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            if(!resultSet.next()) {
                String defaultPrefix = config.getPrefix();
                var addQuery = "INSERT INTO GUILD VALUES(?, ?)";
                var preparedStatement = connection.prepareStatement(addQuery);
                preparedStatement.setString(1, guildId);
                preparedStatement.setString(2, defaultPrefix);
                preparedStatement.execute();
                return defaultPrefix;
            }
            return resultSet.getString("prefix");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createPlaylistForUser(String userId, String playlistName, Collection<TrackData> trackDataMap) throws PlaylistException {

    }

    @Override
    public List<Playlist> getAllPlaylistsForUser(String userId) {
        return null;
    }

    @Override
    public void setPinnedChannelForGuild(String guildId, String channelId) {

    }

    @Override
    public void setPinThresholdForGuild(String guildId, int threshold) {

    }

    @Override
    public void setPrefixForGuild(String guildId, String prefix) {

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
    public Optional<String> getPinnedChannelIdForGuild(String guildId) {
        return Optional.empty();
    }

    @Override
    public int getPinThresholdForGuild(String guildId) {
        return 0;
    }

    @Override
    public void deletePinnedMessageEntryByOriginalMessageId(String guildId, String messageId) {

    }

    @Override
    public void deletePinnedMessageEntryByBotPinnedMessageId(String guildId, String messageId) {

    }

    @Override
    public boolean isMessagePinnedInGuild(String guildId, String originalMessageId) {
        return false;
    }
}
