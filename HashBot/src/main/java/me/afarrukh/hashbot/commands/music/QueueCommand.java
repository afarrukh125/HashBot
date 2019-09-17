package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class QueueCommand extends Command implements MusicCommand {

    public QueueCommand() {
        super("queue");
        addAlias("q");
        addAlias("page");
        description = "Shows the current queue of tracks";
        addParameter("page number", "If there are not enough tracks to fit on first page of the queue, " +
                "the page number can be provided to see the next page(s)");
        addExampleUsage("queue 4");
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        if (params == null)
            evt.getChannel().sendMessage(EmbedUtils.getQueueMsg(Bot.musicManager.getGuildAudioPlayer(evt.getGuild()), evt, 1)).queue();
        else {
            try {
                if (Integer.parseInt(params) == 0) {
                    onIncorrectParams(evt.getChannel());
                    return;
                }
                evt.getChannel().sendMessage(EmbedUtils.getQueueMsg(Bot.musicManager.getGuildAudioPlayer(evt.getGuild()), evt, Integer.parseInt(params))).queue();
            } catch (NumberFormatException e) {
                onIncorrectParams(evt.getChannel());
            }
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: queue/q/page <page number>").queue();
    }
}
