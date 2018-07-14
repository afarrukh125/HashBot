package me.afarrukh.hashbot.utils;

import net.dv8tion.jda.core.entities.Guild;

import java.util.TimerTask;

public class DisconnectTimer extends TimerTask {

    private Guild guild;

    public DisconnectTimer(Guild guild) {
        this.guild = guild;
    }

    @Override
    public void run() {
        MusicUtils.disconnect(guild);
    }
}
