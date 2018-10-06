package me.afarrukh.hashbot.commands.management.bot.owner;

import me.afarrukh.hashbot.commands.Command;

public abstract class OwnerCommand extends Command {

    public OwnerCommand(String name, String[] aliases) {
        super(name, aliases);
    }

    public OwnerCommand(String name) {
        super(name);
    }
}
