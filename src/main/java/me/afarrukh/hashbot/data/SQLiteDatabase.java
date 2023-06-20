package me.afarrukh.hashbot.data;

import me.afarrukh.hashbot.commands.audiotracks.playlist.TrackData;
import me.afarrukh.hashbot.config.Config;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.exceptions.PlaylistException;
import me.afarrukh.hashbot.track.Playlist;
import me.afarrukh.hashbot.track.PlaylistItem;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static java.lang.Runtime.getRuntime;
import static java.util.Collections.singletonList;
import static java.util.Collections.synchronizedList;
import static java.util.Comparator.comparing;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class SQLiteDatabase implements Database {

    private static final String PATH = "HashBot.sqlite";
    private static SQLiteDatabase instance;
    private final Config config;
    private final Connection connection;

    private SQLiteDatabase(Config config) {
        try {
            this.config = config;
            this.connection = DriverManager.getConnection("jdbc:sqlite:%s".formatted(PATH));
            createTablesIfNotPresent();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static SQLiteDatabase getInstance() {
        if (instance == null) {
            instance = new SQLiteDatabase(Bot.getConfig());
        }
        return instance;
    }

    private void createTablesIfNotPresent() {
        try {
            connection
                    .createStatement()
                    .execute(
                            "CREATE TABLE IF NOT EXISTS LISTTRACK(listid VARCHAR(80) REFERENCES PLAYLIST(listid) ON DELETE CASCADE, trackurl VARCHAR(100), position INTEGER)");
            connection
                    .createStatement()
                    .execute(
                            "CREATE TABLE IF NOT EXISTS PLAYLIST(listid VARCHAR(80) PRIMARY KEY, name VARCHAR(60), userid VARCHAR(60))");
            connection
                    .createStatement()
                    .execute("CREATE TABLE IF NOT EXISTS GUILD(id VARCHAR(30), prefix VARCHAR(10))");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Playlist> getPlaylistForUser(String playlistName, String userId) {
        try {
            var query = "SELECT PLAYLIST.name, PLAYLIST.userid, LISTTRACK.trackurl, LISTTRACK.position "
                    + "FROM PLAYLIST, LISTTRACK "
                    + "WHERE LISTTRACK.listid = PLAYLIST.listid "
                    + "AND PLAYLIST.userid = '%s' "
                    + "AND PLAYLIST.name = '%s' "
                    + "ORDER BY LISTTRACK.position";
            query = query.formatted(userId, playlistName);
            var result = connection.createStatement().executeQuery(query);
            if (!result.next()) {
                return Optional.empty();
            }
            List<PlaylistItem> playlistItems = new ArrayList<>();
            playlistItems.add(new PlaylistItem(result.getString("trackurl")));
            while (result.next()) {
                playlistItems.add(new PlaylistItem(result.getString("trackurl")));
            }
            return Optional.of(new Playlist(playlistName, playlistItems));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPrefixForGuild(String guildId) {
        try {
            var query = "SELECT prefix FROM GUILD WHERE GUILD.id = %s".formatted(guildId);
            var resultSet = connection.createStatement().executeQuery(query);
            if (!resultSet.next()) {
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
    public void createPlaylistForUser(String userId, String playlistName, Collection<TrackData> trackDataList)
            throws PlaylistException {
        try {
            var checkQuery = "SELECT PLAYLIST.userid, PLAYLIST.name " + "FROM LISTTRACK, PLAYLIST "
                    + "WHERE PLAYLIST.userid = '%s' "
                    + "AND PLAYLIST.name = '%s'";
            var checkQueryResult =
                    connection.createStatement().executeQuery(checkQuery.formatted(userId, playlistName));
            if (checkQueryResult.next()) {
                throw new PlaylistException("Playlist with name %s already exists".formatted(playlistName));
            }

            var listId = UUID.randomUUID().toString();

            var playlistQuery = connection.prepareStatement("INSERT INTO PLAYLIST VALUES(?, ?, ?)");
            playlistQuery.setString(1, listId);
            playlistQuery.setString(2, playlistName);
            playlistQuery.setString(3, userId);

            List<PreparedStatement> queriesToExecute = new ArrayList<>(singletonList(playlistQuery));

            int position = 0;
            for (var trackData : trackDataList) {
                var trackQuery = connection.prepareStatement("INSERT INTO LISTTRACK VALUES(?, ?, ?)");
                trackQuery.setString(1, listId);
                trackQuery.setString(2, trackData.url());
                trackQuery.setInt(3, position++);
                queriesToExecute.add(trackQuery);
            }

            try (var executor = createExecutorService()) {
                for (var query : queriesToExecute) {
                    executor.execute(() -> {
                        try {
                            query.execute();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Playlist> getAllPlaylistsForUser(String userId) {
        var query = "SELECT PLAYLIST.name, PLAYLIST.userid " +
                "FROM PLAYLIST " +
                "WHERE PLAYLIST.userid=%s";
        query = query.formatted(userId);
        try {
            List<Playlist> playlists = synchronizedList(new ArrayList<>());
            var resultSet = connection.createStatement().executeQuery(query);
            try (var executorService = createExecutorService()) {
                while (resultSet.next()) {
                    var playlistName = resultSet.getString("name");
                    executorService.execute(() -> appendPlaylistIfPresent(playlists, playlistName, userId));
                }
            }
            playlists.sort(comparing(Playlist::getName));
            return playlists;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deletePlaylistForUser(String playlistName, String userId) {
        return false;
    }

    @Override
    public void setPinnedChannelForGuild(String guildId, String channelId) {}

    @Override
    public void setPinThresholdForGuild(String guildId, int threshold) {}

    @Override
    public void setPrefixForGuild(String guildId, String prefix) {}

    @Override
    public void unsetPinnedChannelForGuild(String guildId) {}

    @Override
    public void setMessageAsPinnedInGuild(String guildId, String originalMessageId, String newMessageId) {}

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
    public void deletePinnedMessageEntryByOriginalMessageId(String guildId, String messageId) {}

    @Override
    public void deletePinnedMessageEntryByBotPinnedMessageId(String guildId, String messageId) {}

    @Override
    public boolean isMessagePinnedInGuild(String guildId, String originalMessageId) {
        return false;
    }

    private void appendPlaylistIfPresent(List<Playlist> playlists, String playlistName, String userId) {
        getPlaylistForUser(playlistName, userId).ifPresent(playlists::add);

    }

    @NotNull
    private static ExecutorService createExecutorService() {
        return newFixedThreadPool(getRuntime().availableProcessors() * 2);
    }
}
