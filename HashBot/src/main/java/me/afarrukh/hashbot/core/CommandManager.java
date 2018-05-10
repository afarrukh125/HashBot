package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.commands.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;

public class CommandManager {
    private ArrayList<Command> commandList = new ArrayList<>();

    public void processEvent(MessageReceivedEvent evt) {
        String[] tokens = evt.getMessage().getContentRaw().substring(1).split(" ", 2);
        final String params = (tokens.length > 1) ? tokens[1] : null;
        final String commandName = tokens[0];

        Command command = commandFromName(commandName);
        if(command != null) command.onInvocation(evt, params);

    }

    public CommandManager addCommand(Command c) {
        commandList.add(c);
        return this;
    }

    public Command commandFromName(String name) {
        for(Command c: commandList) {
            if(c.getName().equalsIgnoreCase(name))
                return c;
            if(c.getAliases()!=null)
                for(String alias : c.getAliases())
                    if (alias.equals(name))
                        return c;
        }
        return null;
    }

}
