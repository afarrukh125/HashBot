package me.afarrukh.hashbot.utils;

import java.util.TimerTask;
import me.afarrukh.hashbot.core.AudioTrackManager;
import net.dv8tion.jda.api.entities.Guild;

public class DisconnectTimer extends TimerTask {

    private final Guild guild;
    private final AudioTrackManager audioTrackManager;

    public DisconnectTimer(Guild guild, AudioTrackManager audioTrackManager) {
        this.guild = guild;
        this.audioTrackManager = audioTrackManager;
    }

    @Override
    public void run() {
        AudioTrackUtils.disconnect(guild, audioTrackManager);
    }
}
