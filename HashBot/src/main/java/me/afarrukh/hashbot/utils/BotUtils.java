package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.core.JSONGuildManager;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class BotUtils {

    /**
     * Deletes the last message from a bot in the channel (Hopes that it is the current JDA Bot user)
     * @param evt The event object containing the text channel so we can retrieve the text message
     */
    public static void deleteLastMsg(MessageReceivedEvent evt) {
        for(Message m: evt.getTextChannel().getIterableHistory()) {
            if(m.getAuthor().getId().equals(evt.getJDA().getSelfUser().getId())) {
                m.delete().queueAfter(1500, TimeUnit.MILLISECONDS);
                break;
            }
        }
    }

    public static boolean isPinnedChannel(MessageReceivedEvent evt) {
        JSONGuildManager jgm = new JSONGuildManager(evt.getGuild());
        if(evt.getTextChannel().getId().equals(jgm.getValue("pinnedchannel")))
            return true;
        else
            return false;
    }
}
