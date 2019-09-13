package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.commands.Command;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Abdullah on 08/04/2019 20:22
 */
public class ClearCommand extends Command implements AdminCommand {

    public ClearCommand() {
        super("clear");
        description = "Removes the provided number of messages from the channel it is called in. Use with care.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        if(!evt.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            evt.getTextChannel().sendMessage("This is an administrator only command.").queue();
            return;
        }

        if(params == null) {
            onIncorrectParams(evt.getTextChannel());
            return;
        }

        int amt = 0;
        try {
            amt = Integer.parseInt(params.split(" ")[0]);
        } catch(NumberFormatException e) {
            evt.getTextChannel().sendMessage("You must provide a number of messages to delete.").queue();
            return;
        }

        if(amt > 1000) {
            evt.getTextChannel().sendMessage("You can only delete up to 1000 messages at a time with this command.").queue();
            return;
        }


        List<List<Message>> messageBins = new ArrayList<>();
        Iterator<Message> iter = evt.getTextChannel().getIterableHistory().iterator();

        while(amt > 0) {
            List<Message> messageBin = new LinkedList<>();
            while(messageBin.size() < 100 && amt > 0 && iter.hasNext()) {
                messageBin.add(iter.next());
                amt--;
            }
            messageBins.add(messageBin);
        }

        int msgCount = 0;

        for(List<Message> messageBin: messageBins) {
            msgCount += messageBin.size();
            try {
                evt.getTextChannel().deleteMessages(messageBin).queue();
            } catch (IllegalArgumentException e) {
                String[] msgData = e.getMessage().split(" ");
                String msgId = msgData[msgData.length -1];

                Iterator<Message> binIter = messageBin.iterator();
                while(binIter.hasNext()) {
                    Message delMsg = binIter.next();
                    try {
                        if (delMsg.getIdLong() <= Long.parseLong(msgId)) {
                            binIter.remove();
                            msgCount--;
                        }
                    } catch(NumberFormatException ex) {
                        // Temporary solution to fixing clear command exception, will remove later. [10/04/2019]
                        System.out.println("ClearCommand@onInvocation: " + "```java\n" + ex.getMessage() + "```");
                        System.out.println("ClearCommand@onInvocation: " + "The associated message ID was " + msgId + " and the deleted ID it compared to was " + delMsg.getIdLong());
                    }
                }
                if(messageBin.size() > 0)
                    evt.getTextChannel().deleteMessages(messageBin).queue();
            }
        }

        Message m = evt.getTextChannel().sendMessage("Deleted " + msgCount + " messages.").complete();
        m.delete().queueAfter(2, TimeUnit.SECONDS);
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: clear <number of messages>").queue();
    }
}
