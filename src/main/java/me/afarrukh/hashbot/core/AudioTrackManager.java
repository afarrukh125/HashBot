package me.afarrukh.hashbot.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class AudioTrackManager {
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildAudioTrackManager> trackManagers;

    public AudioTrackManager() {
        this.trackManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerLocalSource(playerManager);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    /**
     * Returns the guild's audio player manager (playerManager instance variable)
     */
    public synchronized GuildAudioTrackManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildAudioTrackManager trackManager = trackManagers.get(guildId); //Gets the current track manager for this guild

        if (trackManager == null) { // If the guild doesn't already have a track manager then create one
            trackManager = new GuildAudioTrackManager(playerManager, guild);
            trackManagers.put(guildId, trackManager);
        }

        guild.getAudioManager().setSendingHandler(trackManager.getSendHandler());
        return trackManager;
    }

    public synchronized void resetGuildAudioPlayer(Guild guild) {
        if (trackManagers.get(guild.getIdLong()) == null)
            return;
        trackManagers.remove(guild.getIdLong());
        trackManagers.put(guild.getIdLong(), new GuildAudioTrackManager(playerManager, guild));


    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }
}
