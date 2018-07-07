package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.Command;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class BotStatsCommand extends Command {

    public BotStatsCommand() {
        super("botstats");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
