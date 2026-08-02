package me.afarrukh.hashbot.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import net.dv8tion.jda.api.entities.Guild;

public class AudioTrackManager {
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildAudioTrackManager> trackManagers;

    public AudioTrackManager(AudioPlayerManager audioPlayerManager) {
        this.trackManagers = new ConcurrentHashMap<>();
        this.playerManager = audioPlayerManager;
    }

    public GuildAudioTrackManager getGuildAudioPlayer(Guild guild) {
        GuildAudioTrackManager trackManager = trackManagers.computeIfAbsent(
                guild.getIdLong(), guildId -> new GuildAudioTrackManager(playerManager, guild, this));

        guild.getAudioManager().setSendingHandler(trackManager.getSendHandler());
        return trackManager;
    }

    public void resetGuildAudioPlayer(Guild guild) {
        trackManagers.computeIfPresent(
                guild.getIdLong(), (guildId, existing) -> new GuildAudioTrackManager(playerManager, guild, this));
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }
}
