package me.afarrukh.hashbot.core;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.TimerTask;

class VoiceExperienceTimer extends TimerTask {

    @Override
    public void run() {

        for (Guild g : Bot.botUser().getGuilds()) {
            for (VoiceChannel vc : g.getVoiceChannels()) {
                if (vc.getMembers().isEmpty()) continue;
                for (Member m : vc.getMembers()) {
                    if (m.getUser().isBot()) continue;
                    if (m.getVoiceState().isMuted() || m.getVoiceState().isDeafened()) continue;
                    if (vc.equals(g.getAfkChannel())) continue;
                    Invoker invoker = Invoker.of(m);
                    invoker.addRandomExperience();
                    invoker.addRandomCredit();
                }
            }
        }

        System.gc();
    }
}
