package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.entities.Invoker;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.TimerTask;

public class VoiceExperienceTimer extends TimerTask {

    @Override
    public void run() {

        for (Guild g : Bot.botUser.getGuilds()) {
            for (VoiceChannel vc : g.getVoiceChannels()) {
                if (vc.getMembers().isEmpty())
                    continue;
                for (Member m : vc.getMembers()) {
                    if (m.getUser().isBot())
                        continue;
                    if (m.getVoiceState().isMuted() || m.getVoiceState().isDeafened())
                        continue;
                    if (vc.equals(g.getAfkChannel()))
                        continue;
                    Invoker invoker = new Invoker(m);
                    invoker.addRandomExperience();
                    invoker.addRandomCredit();
                }
            }
        }

        System.gc();
    }
}
