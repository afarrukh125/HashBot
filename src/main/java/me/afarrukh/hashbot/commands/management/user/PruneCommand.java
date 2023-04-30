package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.pagination.MessagePaginationAction;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static me.afarrukh.hashbot.utils.MessageUtils.deleteAllMessagesFromBin;

public class PruneCommand extends Command {

    public PruneCommand() {
        super("prune");
        addAlias("clean");
        description = "Removes the last 100 bot related messages.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        MessagePaginationAction messageHistory = evt.getChannel().getIterableHistory();
        List<Message> messageBin = new LinkedList<>();
        GuildDataManager gdm = new GuildDataManager(evt.getGuild());
        for (Message m : messageHistory) {
            if (gdm.isBotPinMessage(m.getId())) {
                continue;
            }
            if (m.getAuthor().getId().equals(evt.getJDA().getSelfUser().getId())
                    || m.getContentRaw()
                            .startsWith(Bot.prefixManager
                                    .getGuildRoleManager(evt.getGuild())
                                    .getPrefix())) {
                messageBin.add(m);
            }
            if (messageBin.size() == 100) {
                break;
            }
        }
        try {
            deleteAllMessagesFromBin(evt, messageBin);
            evt.getChannel()
                    .sendMessage("Cleaned " + messageBin.size() + " bot-related messages.")
                    .queueAfter(1500, TimeUnit.MILLISECONDS, message -> message.delete()
                            .queue());
        } catch (IllegalArgumentException e) {
            String[] msgData = e.getMessage().split(" ");
            String msgId = msgData[msgData.length - 1];
            // Removing messages from message bin with id less than the message id that caused the exception
            messageBin.removeIf(delMsg -> delMsg.getIdLong() <= Long.parseLong(msgId));
            int delCount = messageBin.size();
            if (delCount < 2 || delCount > 100) {
                delCount = 0;
            }
            else {
                deleteAllMessagesFromBin(evt, messageBin);
            }
            Message m = evt.getChannel()
                    .sendMessage("Cleaned " + delCount + " bot-related messages.")
                    .complete();
            m.delete().queue();
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: prune <user name> <number of message>").queue();
    }
}
