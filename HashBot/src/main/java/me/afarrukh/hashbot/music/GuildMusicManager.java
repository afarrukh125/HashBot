package me.afarrukh.hashbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.core.entities.Guild;

import java.util.Timer;

/**
 * Object that binds a guild to a player and track scheduler
 */
public class GuildMusicManager {
    private final AudioPlayer player;
    private final TrackScheduler scheduler;
    private Timer disconnectTimer;

    private final Guild guild;

    public GuildMusicManager(AudioPlayerManager manager, Guild guild) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player, guild);
        player.addListener(scheduler);

        this.guild = guild;

        disconnectTimer = new Timer();
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public TrackScheduler getScheduler() {
        return scheduler;
    }

    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }

    public void resetDisconnectTimer() {
        this.disconnectTimer.cancel();
        this.disconnectTimer = new Timer();
    }

    public Timer getDisconnectTimer() {
        return disconnectTimer;
    }

    public Guild getGuild() {
        return guild;
    }
}