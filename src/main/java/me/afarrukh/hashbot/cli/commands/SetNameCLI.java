package me.afarrukh.hashbot.cli.commands;

import me.afarrukh.hashbot.cli.CLICommand;
import me.afarrukh.hashbot.core.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetNameCLI extends CLICommand {
    private static final Logger LOG = LoggerFactory.getLogger(SetNameCLI.class);
    private final Bot bot;

    public SetNameCLI(Bot bot) {
        super("setname");
        addAlias("sn");
        this.bot = bot;
    }

    @Override
    public void onInvocation(String params) {
        if (params == null) {
            LOG.info("You need to provide a username to set");
            return;
        }
        try {
            bot
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
