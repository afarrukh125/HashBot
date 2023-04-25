package me.afarrukh.hashbot.cli.commands;

import me.afarrukh.hashbot.cli.CLICommand;
import me.afarrukh.hashbot.core.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownCLI extends CLICommand {
    private static final Logger LOG = LoggerFactory.getLogger(ShutdownCLI.class);

    public ShutdownCLI() {
        super("shutdown");
        addAlias("exit");
        addAlias("stop");
    }

    @Override
    public void onInvocation(String params) {

        LOG.info("Shutting down the bot...");
        Bot.botUser().shutdown();
        LOG.info("Shut down successfully.");
        System.exit(0);
    }
}
