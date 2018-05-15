package me.afarrukh.hashbot.commands.management.bot.owner;

import me.afarrukh.hashbot.commands.Command;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class OwnerCommand extends Command {

    public OwnerCommand(String name, String[] aliases) {
        super(name, aliases);
    }

    public OwnerCommand(String name) {
        super(name);
    }
}
