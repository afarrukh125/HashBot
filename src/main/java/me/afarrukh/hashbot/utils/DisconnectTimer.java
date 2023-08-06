package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.core.AudioTrackManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.TimerTask;

public class DisconnectTimer extends TimerTask {

    private final Guild guild;
    private AudioTrackManager audioTrackManager;

    public DisconnectTimer(Guild guild, AudioTrackManager audioTrackManager) {
        this.guild = guild;
        this.audioTrackManager = audioTrackManager;
    }

    @Override
    public void run() {
        AudioTrackUtils.disconnect(guild, audioTrackManager);
    }
}
