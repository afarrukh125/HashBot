package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static java.lang.Integer.parseInt;

public class QueueCommand extends Command implements AudioTrackCommand {

    private final Database database;

    public QueueCommand(Database database) {
        super("queue");
        this.database = database;
        addAlias("q");
        addAlias("page");
        description = "Shows the current queue of tracks";
        addParameter(
                "page number",
                "If there are not enough tracks to fit on first page of the queue, "
                        + "the page number can be provided to see the next page(s)");
        addExampleUsage("queue 4");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (params == null)
            evt.getChannel()
                    .sendMessageEmbeds(
                            EmbedUtils.getQueueMsg(Bot.trackManager.getGuildAudioPlayer(evt.getGuild()), evt, 1))
                    .queue();
        else {
            try {
                if (parseInt(params) == 0) {
                    onIncorrectParams(database, evt.getChannel().asTextChannel());
                    return;
                }
                evt.getChannel()
                        .sendMessageEmbeds(EmbedUtils.getQueueMsg(
                                Bot.trackManager.getGuildAudioPlayer(evt.getGuild()), evt, parseInt(params)))
                        .queue();
            } catch (NumberFormatException e) {
                onIncorrectParams(database, evt.getChannel().asTextChannel());
            }
        }
    }

    @Override
    public void onIncorrectParams(Database database, TextChannel channel) {
        channel.sendMessage("Usage: queue/q/page <page number>").queue();
    }
}
