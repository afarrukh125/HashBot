package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.utils.BotUtils;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.restaction.pagination.MessagePaginationAction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PruneCommand extends Command {

    public PruneCommand() {
        super("prune");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        MessagePaginationAction messageHistory = evt.getTextChannel().getIterableHistory();
        List<Message> messageBin = new ArrayList<>();
        for(Message m: messageHistory) {
            if(m.getAuthor().getId().equals(evt.getJDA().getSelfUser().getId()) || m.getContentRaw().startsWith(Constants.invokerChar)) {
                messageBin.add(m);
            }
            if(messageBin.size() == 100)
                break;
        }
        try {
            evt.getTextChannel().deleteMessages(messageBin).queue();
            evt.getChannel().sendMessage("Cleaned. :ok_hand:").queue();
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
            evt.getTextChannel().deleteMessages(messageBin).queue();
            evt.getChannel().sendMessage("Cleaned. :ok_hand:").queue();
            BotUtils.deleteLastMsg(evt);
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: prune <user name> <number of message>").queue();
    }
}
