package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PingCommand extends Command {

    public PingCommand() {super("ping"); }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        evt.getTextChannel().sendMessage("Current ping is " +evt.getJDA().getPing()).queue();
    }
}
