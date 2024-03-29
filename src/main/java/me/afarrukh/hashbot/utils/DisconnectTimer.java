package me.afarrukh.hashbot.utils;

import net.dv8tion.jda.api.entities.Guild;

import java.util.TimerTask;

public class DisconnectTimer extends TimerTask {

    private final Guild guild;

    public DisconnectTimer(Guild guild) {
        this.guild = guild;
    }

    @Override
    public void run() {
        AudioTrackUtils.disconnect(guild);
    }
}
