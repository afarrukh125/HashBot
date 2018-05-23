package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.management.bot.owner.OwnerCommand;
import me.afarrukh.hashbot.utils.UserUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;

public class CommandManager {
    private final ArrayList<Command> commandList = new ArrayList<>();

    public void processEvent(MessageReceivedEvent evt) {
        String[] tokens = evt.getMessage().getContentRaw().substring(1).split(" ", 2);
        final String params = (tokens.length > 1) ? tokens[1] : null;
        final String commandName = tokens[0];

        Command command = commandFromName(commandName);

        if(command instanceof OwnerCommand && command!=null && !UserUtils.isBotAdmin(evt.getAuthor()))
            return;

        if(command != null) command.onInvocation(evt, params);

    }

    public CommandManager addCommand(Command c) {
        commandList.add(c);
        return this;
    }

    private Command commandFromName(String name) {
        for(Command c: commandList) {
            if(c.getName().equalsIgnoreCase(name))
                return c;
            if(c.getAliases()!=null)
                for(String alias : c.getAliases())
                    if (alias.equalsIgnoreCase(name))
                        return c;
        }
        return null;
    }

    public ArrayList<Command> getCommandList() {
        return new ArrayList<>(commandList);
    }

}
