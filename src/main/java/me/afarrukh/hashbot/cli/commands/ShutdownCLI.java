package me.afarrukh.hashbot.cli.commands;

import me.afarrukh.hashbot.cli.CLICommand;
import me.afarrukh.hashbot.core.Bot;

/**
 * @author Abdullah
 * Created on 06/09/2019 at 18:39
 * Shuts down the bot
 */
public class ShutdownCLI extends CLICommand {
    public ShutdownCLI() {
        super("shutdown");
        addAlias("exit");
        addAlias("stop");
    }

    @Override
    public void onInvocation(String params) {

        System.out.println("Shutting down the bot...");
        Bot.botUser.shutdown();
        System.out.println("Shut down successfully.");
        System.exit(0);
    }
}
