package me.afarrukh.hashbot.cli.commands;

import me.afarrukh.hashbot.cli.CLICommand;
import me.afarrukh.hashbot.core.Bot;

public class ShutdownCLI extends CLICommand {
    public ShutdownCLI() {
        super("shutdown");
        addAlias("exit");
        addAlias("stop");
    }

    @Override
    public void onInvocation(String params) {

        System.out.println("Shutting down the bot...");
        Bot.botUser().shutdown();
        System.out.println("Shut down successfully.");
        System.exit(0);
    }
}
