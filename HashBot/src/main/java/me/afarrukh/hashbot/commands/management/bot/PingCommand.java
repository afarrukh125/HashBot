package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.SystemCommand;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class PingCommand extends Command implements SystemCommand {

    public PingCommand() {
        super("ping");
        description = "Returns the latency in milliseconds between the discord servers and the bot. This does not mean this is your ping";
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        evt.getChannel().sendMessage("Current ping is " + evt.getJDA().getPing()).queue();
    }
}
