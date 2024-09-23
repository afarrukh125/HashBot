package me.afarrukh.hashbot.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import java.util.HashMap;
import java.util.Map;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import net.dv8tion.jda.api.entities.Guild;

public class AudioTrackManager {
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildAudioTrackManager> trackManagers;

    public AudioTrackManager() {
        this.trackManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());

        AudioSourceManagers.registerLocalSource(playerManager);
        AudioSourceManagers.registerRemoteSources(playerManager);
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
