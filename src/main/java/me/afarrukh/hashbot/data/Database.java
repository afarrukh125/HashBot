package me.afarrukh.hashbot.data;

import me.afarrukh.hashbot.commands.audiotracks.playlist.TrackData;
import me.afarrukh.hashbot.track.Playlist;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Database {

    static Database getInstance() {
        return new Neo4jDatabase();
    }

    Optional<Playlist> getPlaylistForUser(String playlistName, String userId);

    void deletePlaylistForUser(String playlistName, String userId);

    String getPrefixForGuild(String guildId);

    void createPlaylistForUser(String userId, String playlistName, Map<String, TrackData> trackDataMap);

    List<Playlist> getAllPlaylistsForUser(String userId);

    void setPinnedChannelForGuild(String guildId, String channelId);

    void setPinThresholdForGuild(String id, int threshold);

    void setPrefixForGuild(String guildiD, String prefix);

    void unsetPinnedChannelForGuild(String guildId);

    void setMessageAsPinnedInGuild(String guildId, String originalMessageId, String newMessageId);

    boolean isBotPinMessageInGuild(String guildId, String messageId);

    Optional<String> getPinnedChannelIdForGuild(String id);

    int getPinThresholdForGuild(String id);

    void deletePinnedMessageEntryByOriginalMessageId(String guildId, String messageId);

    void deletePinnedMessageEntryByBotPinnedMessageId(String guildId, String messageId);
}
