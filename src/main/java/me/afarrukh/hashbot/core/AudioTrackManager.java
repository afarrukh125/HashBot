package me.afarrukh.hashbot.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import java.util.HashMap;
import java.util.Map;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import net.dv8tion.jda.api.entities.Guild;

public class AudioTrackManager {
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildAudioTrackManager> trackManagers;

    public AudioTrackManager(AudioPlayerManager audioPlayerManager) {
        this.trackManagers = new HashMap<>();
        this.playerManager = audioPlayerManager;
    }

    public GuildAudioTrackManager getGuildAudioPlayer(Guild guild) {
        long guildId = guild.getIdLong();
        GuildAudioTrackManager trackManager = trackManagers.get(guildId);

        if (trackManager == null) {
            trackManager = new GuildAudioTrackManager(playerManager, guild, this);
            trackManagers.put(guildId, trackManager);
        }

        guild.getAudioManager().setSendingHandler(trackManager.getSendHandler());
        return trackManager;
    }

    public void resetGuildAudioPlayer(Guild guild) {
        if (trackManagers.get(guild.getIdLong()) == null) {
            return;
        }
        trackManagers.remove(guild.getIdLong());
        trackManagers.put(guild.getIdLong(), new GuildAudioTrackManager(playerManager, guild, this));
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }
}
