package me.afarrukh.hashbot.core.cli;

import me.afarrukh.hashbot.core.cli.commands.CheckMemoryCLI;
import me.afarrukh.hashbot.core.cli.commands.GarbageCleanCLI;
import me.afarrukh.hashbot.core.cli.commands.ShutdownCLI;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Abdullah
 * Created on 06/09/2019 at 18:39
 */
public class CommandLineInputManager {

    private Map<String, CLICommand> commandMap;

    public CommandLineInputManager() {
        commandMap = new HashMap<>();

        addCommand(new CheckMemoryCLI());
        addCommand(new GarbageCleanCLI());
        addCommand(new ShutdownCLI());
    }

    public void processInput(String input) {
        CLICommand commandToExecute = commandMap.get(input);

        String[] tokens = input.split(" ", 2);
        final String params = (tokens.length > 1) ? tokens[1] : null;
        if (commandToExecute == null)
            System.out.println("Invalid command: " + input);
        else
            commandToExecute.onInvocation(params);
    }

    private void addCommand(CLICommand command) {
        commandMap.put(command.getName(), command);
        for (String alias : command.getAliases())
            commandMap.put(alias, command);
    }
}
