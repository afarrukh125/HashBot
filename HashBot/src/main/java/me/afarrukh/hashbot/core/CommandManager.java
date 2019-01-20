package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.commands.AdminCommand;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.management.bot.owner.OwnerCommand;
import me.afarrukh.hashbot.utils.UserUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.*;

public class CommandManager {

    private final Map<String, Command> commandMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /**
     * Each time a message is sent with the bot prefix is sent, this method is called to check if a command exists
     * with that command name
     * @param evt The message received event associated with the possible command invocation
     */
    public void processEvent(MessageReceivedEvent evt) {
        String[] tokens = evt.getMessage().getContentRaw().substring(1).split(" ", 2);
        final String params = (tokens.length > 1) ? tokens[1] : null;
        final String commandName = tokens[0];

        Command command = commandFromName(commandName);

        if(command instanceof OwnerCommand && !UserUtils.isBotAdmin(evt.getAuthor()))
            return;

        if(command instanceof AdminCommand && !evt.getMember().hasPermission(Permission.ADMINISTRATOR))
            return;

        if(command != null) command.onInvocation(evt, params); //Only does the command onInvocation method if
                                                               //the command is valid

    }

    /**
     * Adds a command to the command manager
     * @param c The command to be added to the command map
     * @return The command manager itself (Allows for chained addCommand calls
     */
    public CommandManager addCommand(Command c) {
        commandMap.put(c.getName(), c);
        if(!c.getAliases().isEmpty()) {
            for(String alias: c.getAliases()) {
                commandMap.put(alias, c);
            }
        }
        return this;
    }

    /**
     * Queries the command manager with a string and returns a command associated with that string
     * @param name The name of the command or one of the aliases to get a command from
     * @return The command with that associated name in the command map, null otherwise
     */
    private Command commandFromName(String name) {
        return commandMap.get(name);
    }

    /**
     * Returns a new ArrayList with all the commands list, with duplicates omitted
     * @return A new ArrayList with all commands, derived from the commandsMap
     */
    public List<Command> getCommandList() {
        List<Command> commandList = new ArrayList<>();
        for(Command c: commandMap.values()) {
            if(commandList.contains(c))
                continue;
            commandList.add(c);
        }
        return commandList;
    }

    public List<Command> getNonAdminCommands() {
        List<Command> commandList = new LinkedList<>();

        for(Command c: getCommandList()) {
            if (!(c instanceof AdminCommand))
                commandList.add(c);
        }

        return new ArrayList<>(commandList);
    }

}
