package me.afarrukh.hashbot.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import me.afarrukh.hashbot.music.GuildMusicManager;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class MusicManager {
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    public MusicManager() {
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerLocalSource(playerManager);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    /**
     * Returns the guild's audio player manager (playerManager instance variable)
     */
    public synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId); //Gets the current music manager for this guild

        if(musicManager == null) { // If the guild doesn't already have a music manager then create one
            musicManager = new GuildMusicManager(playerManager, guild);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        return musicManager;
    }

    public synchronized void resetGuildAudioPlayer(Guild guild) {
        if(musicManagers.get(guild.getIdLong()) == null)
            return;
        musicManagers.remove(guild.getIdLong());
        musicManagers.put(guild.getIdLong(), new GuildMusicManager(playerManager, guild));


    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }
}
