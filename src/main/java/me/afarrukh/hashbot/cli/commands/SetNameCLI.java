package me.afarrukh.hashbot.cli.commands;

import com.google.inject.Inject;
import me.afarrukh.hashbot.cli.CLICommand;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetNameCLI extends CLICommand {
    private static final Logger LOG = LoggerFactory.getLogger(SetNameCLI.class);
    private final JDA jda;

    public SetNameCLI(JDA jda) {
        super("setname");
        this.jda = jda;
        addAlias("sn");
    }

    @Override
    public void onInvocation(String params) {
        if (params == null) {
            LOG.info("You need to provide a username to set");
            return;
        }

        try {
            jda
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
