package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.utils.BotUtils;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.restaction.pagination.MessagePaginationAction;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PruneCommand extends Command {

    public PruneCommand() {
        super("prune");
        addAlias("clean");
        description = "Removes the last 100 bot related messages.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        MessagePaginationAction messageHistory = evt.getTextChannel().getIterableHistory();
        List<Message> messageBin = new LinkedList<>();
        GuildDataManager gdm = new GuildDataManager(evt.getGuild());
        for(Message m: messageHistory) {
            if(gdm.isBotPinMessage(m.getId())) // Do not want to clear any bot pinned message
                continue;
            if(m.getAuthor().getId().equals(evt.getJDA().getSelfUser().getId()) || m.getContentRaw().startsWith(Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPrefix())) {
                messageBin.add(m);
            }
            if(messageBin.size() == 100)
                break;
        }
        try {
            evt.getTextChannel().deleteMessages(messageBin).queue();
            evt.getChannel().sendMessage("Cleaned " +messageBin.size() + " bot-related messages.").queue();
            BotUtils.deleteLastMsg(evt);
        } catch(IllegalArgumentException e) {
            String[] msgData = e.getMessage().split(" ");
            String msgId = msgData[msgData.length -1];
            Iterator<Message> iter = messageBin.iterator();
            //Removing messages from message bin with id less than the message id that caused the exception
            while(iter.hasNext()) {
                Message delMsg = iter.next();
                if(delMsg.getIdLong() <= Long.parseLong(msgId))
                    iter.remove();
            }
            int delCount = messageBin.size();
            if(delCount < 2 || delCount > 100)
                delCount = 0;
            else
                evt.getTextChannel().deleteMessages(messageBin).queue();
            Message m = evt.getChannel().sendMessage("Cleaned " +delCount + " bot-related messages.").complete();
            m.delete().queue();
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: prune <user name> <number of message>").queue();
    }
}
