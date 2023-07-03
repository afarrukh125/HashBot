package me.afarrukh.hashbot.cli.commands;

import com.google.inject.Guice;
import me.afarrukh.hashbot.cli.CLICommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.module.CoreBotModule;
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
        var injector = Guice.createInjector(new CoreBotModule());
        LOG.info("Shutting down the bot...");
        injector.getInstance(Bot.class).getBotUser().shutdown();
        LOG.info("Shut down successfully.");
        System.exit(0);
    }
}
