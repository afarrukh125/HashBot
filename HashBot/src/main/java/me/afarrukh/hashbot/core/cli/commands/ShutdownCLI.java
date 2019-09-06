package me.afarrukh.hashbot.core.cli.commands;

import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.cli.CLICommand;

/**
 * @author Abdullah
 * Created on 06/09/2019 at 18:39
 * Shuts down the bot
 */
public class ShutdownCLI extends CLICommand {
    public ShutdownCLI() {
        super("shutdown");
    }

    @Override
    public void onInvocation(String params) {

        Bot.botUser.shutdown();
        System.exit(0);
    }
}
