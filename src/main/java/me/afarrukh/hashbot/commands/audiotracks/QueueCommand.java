package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static java.lang.Integer.parseInt;

public class QueueCommand extends Command implements AudioTrackCommand {

    public QueueCommand() {
        super("queue");
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
        var injector = Guice.createInjector(new CoreBotModule());
        var trackManager = injector.getInstance(AudioTrackManager.class);
        if (params == null)
            evt.getChannel()
                    .sendMessageEmbeds(EmbedUtils.getQueueMsg(trackManager.getGuildAudioPlayer(evt.getGuild()), evt, 1))
                    .queue();
        else {
            try {
                if (parseInt(params) == 0) {
                    onIncorrectParams(evt.getChannel().asTextChannel());
                    return;
                }
                evt.getChannel()
                        .sendMessageEmbeds(EmbedUtils.getQueueMsg(
                                trackManager.getGuildAudioPlayer(evt.getGuild()), evt, parseInt(params)))
                        .queue();
            } catch (NumberFormatException e) {
                onIncorrectParams(evt.getChannel().asTextChannel());
            }
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: queue/q/page <page number>").queue();
    }
}
