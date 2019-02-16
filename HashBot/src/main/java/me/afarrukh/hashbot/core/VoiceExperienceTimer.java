package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.entities.Invoker;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.Iterator;
import java.util.TimerTask;

public class VoiceExperienceTimer extends TimerTask {

    @Override
    public void run() {
        Iterator<Guild> iter = Bot.botUser.getGuilds().iterator();

        while(iter.hasNext()) {
            Guild g = iter.next();
            for(VoiceChannel vc: g.getVoiceChannels()) {
                if(vc.getMembers().isEmpty())
                    continue;
                for(Member m: vc.getMembers()) {
                    if(m.getUser().isBot())
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
