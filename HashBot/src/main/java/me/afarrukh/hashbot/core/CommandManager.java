package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.management.bot.owner.OwnerCommand;
import me.afarrukh.hashbot.utils.UserUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class CommandManager {

    private final HashMap<String, Command> commandMap = new HashMap<>();

    public void processEvent(MessageReceivedEvent evt) {
        String[] tokens = evt.getMessage().getContentRaw().substring(1).split(" ", 2);
        final String params = (tokens.length > 1) ? tokens[1] : null;
        final String commandName = tokens[0];

        Command command = commandFromName(commandName);

        if(command instanceof OwnerCommand && !UserUtils.isBotAdmin(evt.getAuthor()))
            return;

        if(command != null) command.onInvocation(evt, params);

    }

    public CommandManager addCommand(Command c) {
        commandMap.put(c.getName(), c);
        return this;
    }

    private Command commandFromName(String name) {
        Command command = commandMap.get(name);

        if(command == null) {
            for(Command c: commandMap.values()) {
                if(c.getAliases() == null)
                    continue;
                for(String s: c.getAliases())
                    if(s.equalsIgnoreCase(name))
                        return c;
            }
            return null;
        }
        else
            return command;
    }

    public ArrayList<Command> getCommandList() {
        ArrayList<Command> commandList = new ArrayList<>(commandMap.values());
        commandList.sort(new Comparator<Command>() {
            @Override
            public int compare(Command c1, Command c2) {
                if(c1.getName().charAt(0) < c2.getName().charAt(0))
                    return -1;
                return 1;
            }
        });
        return new ArrayList<>(commandList);
    }

}
