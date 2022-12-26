package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.commands.tagging.OwnerCommand;
import me.afarrukh.hashbot.utils.UserUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {

    private final Map<String, Command> map;

    private int commandCount = 0;

    private CommandManager(Builder builder) {
        this.map = builder.map;
    }

    /**
     * Each time a message is sent with the bot prefix is sent, this method is called to check if a command exists
     * with that command name
     *
     * @param evt The message received event associated with the possible command invocation
     */
    public void processEvent(MessageReceivedEvent evt) {
        String[] tokens = evt.getMessage().getContentRaw().substring(1).split(" ", 2);
        final String params = (tokens.length > 1) ? tokens[1].trim() : null;
        final String commandName = tokens[0].toLowerCase();

        Command command = commandFromName(commandName);

        if (command instanceof OwnerCommand && UserUtils.isBotAdmin(evt.getAuthor())) {
            return;
        }

        if (command instanceof AdminCommand && !evt.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            return;
        }

        if (command != null) {
            this.commandCount += 1;
            new Thread(() -> {
                command.onInvocation(evt, params); //Only does the command onInvocation method if class is valid
            }).start();
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
            if (commandList.contains(c))
                continue;
            commandList.add(c);
        }
        commandList.sort(Comparator.comparing(Command::getName).reversed());
        return commandList;
    }

    public List<Command> getNonAdminCommands() {
        List<Command> commandList = new LinkedList<>();

        for (Command c : getCommands()) {
            if (!(c instanceof AdminCommand))
                commandList.add(c);
        }

        return new ArrayList<>(commandList);
    }

    public static class Builder {

        private final Map<String, Command> map = new ConcurrentHashMap<>();

        public CommandManager build() {
            return new CommandManager(this);
        }

        public Builder addCommand(Command c) {
            map.put(c.getName(), c);
            new Thread(() -> {
                if (!c.getAliases().isEmpty()) {
                    for (String alias : c.getAliases()) {
                        map.put(alias, c);
                    }
                }
            }).start();
            return this;
        }

    }

}
