package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.utils.BotUtils;
import me.afarrukh.hashbot.utils.CmdUtils;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Iterator;

public class PruneCommand extends Command {

    public PruneCommand() {
        super("prune");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        String[] tokens = params.split(" ");

        String name = CmdUtils.getParamsAsString(tokens, 0, tokens.length-2);
        int numMsgs = 0;
        try {
            numMsgs = Integer.parseInt(tokens[tokens.length - 1]);
        } catch(NumberFormatException e) {
            onIncorrectParams(evt.getTextChannel());
        }

        if(numMsgs > Constants.ITERABLE_MESSAGES) {
            evt.getChannel().sendMessage("You cannot provide more than 500 messages to delete at one time.").queue();
            return;
        }

        Iterator<Message> iter = evt.getTextChannel().getIterableHistory().iterator();
        int binCount = 0;
        for(int i = 0; i<numMsgs/100; i++) {
            ArrayList<Message> messageBin = new ArrayList<>();
            int iteratedCount = 0;

            while(iter.hasNext() && iteratedCount<100) {
                Message m = iter.next();
                if(m.getAuthor().getName().equals(name))
                    messageBin.add(m);
                iteratedCount++;
            }

            if(messageBin.isEmpty()) {
                break;
            }
            evt.getTextChannel().deleteMessages(messageBin).queue();
            binCount += messageBin.size();
        }
        evt.getTextChannel().sendMessage("Deleted " +binCount+ " messages.").queue();
        BotUtils.deleteLastMsg(evt);
        evt.getMessage().delete().queue();


    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: prune <user name> <number of message>").queue();
    }
}
