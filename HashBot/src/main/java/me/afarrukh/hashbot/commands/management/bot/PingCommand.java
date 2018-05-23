package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.Command;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping");
        description = "Returns the latency in milliseconds between the discord servers and the bot. This does not mean this is your ping";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        evt.getTextChannel().sendMessage("Current ping is " +evt.getJDA().getPing()).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {}
}
