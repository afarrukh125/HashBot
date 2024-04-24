package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.CommandManagerModule;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.commands.tagging.OwnerCommand;
import me.afarrukh.hashbot.config.Config;
import me.afarrukh.hashbot.utils.UserUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Comparator.comparing;

public class CommandManager {

    private final Map<String, Command> map;

    private int commandCount;

    public CommandManager() {
        map = new HashMap<>();
    }

    public CommandManager withCommand(Command command) {
        map.put(command.getName(), command);
        if (!command.getAliases().isEmpty()) {
            for (String alias : command.getAliases()) {
                map.put(alias, command);
            }
        }

        return this;
    }

    public void processEvent(MessageReceivedEvent evt, Config config) {
        String[] tokens = evt.getMessage().getContentRaw().substring(1).split(" ", 2);
        final String params = (tokens.length > 1) ? tokens[1].trim() : null;
        final String commandName = tokens[0].toLowerCase();

        Command command = commandFromName(commandName);

        if (command instanceof OwnerCommand && UserUtils.isBotAdmin(evt.getAuthor(), config)) {
            return;
        }

        if (command instanceof AdminCommand && !evt.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            return;
        }

        if (command != null) {
            this.commandCount += 1;
            command.onInvocation(evt, params);
        }
    }

    public int getCommandCount() {
        return commandCount;
    }

    public Command commandFromName(String name) {
        return map.get(name);
    }

    public List<Command> getCommands() {
        List<Command> commandList = new ArrayList<>();
        for (Command c : map.values()) {
            if (commandList.contains(c)) {
                continue;
            }
            commandList.add(c);
        }
        commandList.sort(comparing(Command::getName).reversed());
        return commandList;
    }

    public List<Command> getNonAdminCommands() {
        List<Command> commandList = new LinkedList<>();

        for (Command c : getCommands()) {
            if (!(c instanceof AdminCommand)) {
                commandList.add(c);
            }
        }

        return new ArrayList<>(commandList);
    }
}
