package me.afarrukh.hashbot.data;

import me.afarrukh.hashbot.commands.audiotracks.playlist.TrackData;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.exceptions.PlaylistException;
import me.afarrukh.hashbot.track.Playlist;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface Database {

    static Database getInstance() {
        try {
            return new Neo4jDatabase(Bot.getConfig());
        } catch (Exception e) {
            return new SQLiteDatabase(Bot.getConfig());
        }
    }

    Optional<Playlist> getPlaylistForUser(String playlistName, String userId);

    boolean deletePlaylistForUser(String playlistName, String userId);

    String getPrefixForGuild(String guildId);

    void createPlaylistForUser(String userId, String playlistName, Collection<TrackData> trackDataMap)
            throws PlaylistException;

    List<Playlist> getAllPlaylistsForUser(String userId);

    void setPinnedChannelForGuild(String guildId, String channelId);

    void setPinThresholdForGuild(String guildId, int threshold);

    void setPrefixForGuild(String guildId, String prefix);

    void unsetPinnedChannelForGuild(String guildId);

    void setMessageAsPinnedInGuild(String guildId, String originalMessageId, String newMessageId);

    boolean isBotPinMessageInGuild(String guildId, String messageId);

    Optional<String> getPinnedChannelIdForGuild(String guildId);

    int getPinThresholdForGuild(String guildId);

    void deletePinnedMessageEntryByOriginalMessageId(String guildId, String messageId);

    void deletePinnedMessageEntryByBotPinnedMessageId(String guildId, String messageId);

    boolean isMessagePinnedInGuild(String guildId, String originalMessageId);
}
