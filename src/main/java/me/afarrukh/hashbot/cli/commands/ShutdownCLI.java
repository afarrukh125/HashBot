package me.afarrukh.hashbot.cli.commands;

import me.afarrukh.hashbot.cli.CLICommand;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownCLI extends CLICommand {
    private static final Logger LOG = LoggerFactory.getLogger(ShutdownCLI.class);
    private final JDA jda;

    public ShutdownCLI(JDA jda) {
        super("shutdown");
        this.jda = jda;
        addAlias("exit");
        addAlias("stop");
    }

    @Override
    public void onInvocation(String params) {

        LOG.info("Shutting down the bot...");
        jda.shutdown();
        LOG.info("Shut down successfully.");
        System.exit(0);
    }
}
