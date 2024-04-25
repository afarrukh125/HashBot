package me.afarrukh.hashbot.track;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import me.afarrukh.hashbot.core.AudioTrackManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Timer;

/**
 * Object that binds a guild to a player and track scheduler
 */
public class GuildAudioTrackManager {
    private final AudioPlayer player;
    private final TrackScheduler scheduler;
    private final Guild guild;
    private Timer disconnectTimer;

    public GuildAudioTrackManager(AudioPlayerManager manager, Guild guild, AudioTrackManager audioTrackManager) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player, guild, audioTrackManager);
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