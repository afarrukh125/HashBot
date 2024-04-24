package me.afarrukh.hashbot.cli;

import com.google.inject.Inject;
import me.afarrukh.hashbot.cli.commands.CheckMemoryCLI;
import me.afarrukh.hashbot.cli.commands.GarbageCleanCLI;
import me.afarrukh.hashbot.cli.commands.SetNameCLI;
import me.afarrukh.hashbot.cli.commands.ShutdownCLI;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CommandLineInputManager {
    private static final Logger LOG = LoggerFactory.getLogger(CommandLineInputManager.class);
    private final Map<String, CLICommand> commandMap;

    @Inject
    public CommandLineInputManager(JDA jda) {
        commandMap = new HashMap<>();
        addCommand(new CheckMemoryCLI());
        addCommand(new GarbageCleanCLI());
        addCommand(new ShutdownCLI(jda));
        addCommand(new SetNameCLI(jda));
    }

    public void processInput(String input) {
        String commandFromString = input.split(" ")[0];
        CLICommand commandToExecute = commandMap.get(commandFromString);

        String[] tokens = input.split(" ", 2);
        final String params = (tokens.length > 1) ? tokens[1] : null;
        if (commandToExecute == null) {
            LOG.error("Invalid command: {}", input);
        } else {
            commandToExecute.onInvocation(params);
        }
    }

    private void addCommand(CLICommand command) {
        commandMap.put(command.getName(), command);
        for (String alias : command.getAliases()) commandMap.put(alias, command);
    }
}
