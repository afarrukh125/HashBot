package me.afarrukh.hashbot.cli.commands;

import com.google.inject.Guice;
import me.afarrukh.hashbot.cli.CLICommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetNameCLI extends CLICommand {
    private static final Logger LOG = LoggerFactory.getLogger(SetNameCLI.class);

    public SetNameCLI() {
        super("setname");
        addAlias("sn");
    }

    @Override
    public void onInvocation(String params) {
        if (params == null) {
            LOG.info("You need to provide a username to set");
            return;
        }
        var injector = Guice.createInjector(new CoreBotModule());
        try {
            injector.getInstance(Bot.class)
                    .getBotUser()
                    .getSelfUser()
                    .getManager()
                    .setName(params)
                    .queue(aVoid -> LOG.info("Global name changed to {}", params), throwable -> {
                        LOG.error("Name change to {} failed: {}", params, throwable.getMessage());
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
