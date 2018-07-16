package me.afarrukh.hashbot.utils;

import net.dv8tion.jda.core.entities.Guild;

import java.util.TimerTask;

public class DisconnectTimer extends TimerTask {

    private final Guild guild;

    public DisconnectTimer(Guild guild) {
        this.guild = guild;
    }

    @Override
    public void run() {
        System.out.println("Bot was disconnected from " +guild.getName()+ " because either " +
                "no users were in the channel for more than 30 seconds while it was paused" +
                "or the track ended and none were queued" +
                "after 30 seconds");
        MusicUtils.disconnect(guild);
    }
}
