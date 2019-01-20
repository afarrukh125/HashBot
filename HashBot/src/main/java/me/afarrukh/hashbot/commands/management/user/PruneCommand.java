package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.BotUtils;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.restaction.pagination.MessagePaginationAction;
import net.dv8tion.jda.core.requests.restaction.pagination.PaginationAction;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PruneCommand extends Command {

    public PruneCommand() {
        super("prune");
        addAlias("clean");
        description = "Removes the last 100 bot related messages.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        evt.getMessage().delete().queue();
        MessagePaginationAction history = evt.getTextChannel().getIterableHistory();
        PaginationAction<Message, MessagePaginationAction>.PaginationIterator iter = history.iterator();
        int count = 0;
        List<Message> messageBin = new LinkedList<>();

        while(iter.hasNext() && count < 100) {
            Message m = iter.next();
            if(System.currentTimeMillis() - m.getCreationTime().toInstant().toEpochMilli() > Constants.UNIXTWOWEEKS)
                break;

            if(m.getContentRaw().startsWith(Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPrefix())) {
                messageBin.add(m); count++;
            }
            if(m.getAuthor().equals(evt.getJDA().getSelfUser())) {
                messageBin.add(m); count++;
            }
        }
        if(messageBin.size() > 2) {
            evt.getTextChannel().deleteMessages(messageBin).queue();
            Message m = evt.getTextChannel().sendMessage("Deleted " +count+ " bot-related messages").complete();
            m.delete().queueAfter(2, TimeUnit.SECONDS);
        } else {
            Message m = evt.getTextChannel().sendMessage("Could not find any messages to clean.").complete();
            m.delete().queueAfter(2, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: prune <user name> <number of message>").queue();
    }
}
