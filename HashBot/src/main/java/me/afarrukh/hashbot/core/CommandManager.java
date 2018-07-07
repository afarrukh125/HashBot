package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.management.bot.owner.OwnerCommand;
import me.afarrukh.hashbot.utils.UserUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class CommandManager {

    private final Map<String, Command> commandMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

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
        if(c.getAliases() != null) {
            for(String alias: c.getAliases()) {
                commandMap.put(alias, c);
            }
        }
        return this;
    }

    private Command commandFromName(String name) {
        return commandMap.get(name);
    }

    public ArrayList<Command> getCommandList() {
        ArrayList<Command> commandList = new ArrayList<>();
        for(Command c: commandMap.values()) {
            if(commandList.contains(c))
                continue;
            commandList.add(c);
        }
        return new ArrayList<>(commandList);
    }

}
