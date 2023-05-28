package me.afarrukh.hashbot.data;

import me.afarrukh.hashbot.commands.audiotracks.playlist.TrackData;
import me.afarrukh.hashbot.config.Config;
import me.afarrukh.hashbot.exceptions.PlaylistException;
import me.afarrukh.hashbot.track.Playlist;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SQLiteDatabase implements Database {
    private final Config config;

    public SQLiteDatabase(Config config) {
        this.config = config;
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
        return null;
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
