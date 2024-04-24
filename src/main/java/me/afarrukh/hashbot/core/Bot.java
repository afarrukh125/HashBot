package me.afarrukh.hashbot.core;

import com.google.inject.Inject;
import me.afarrukh.hashbot.commands.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Bot {
    private static final Logger LOG = LoggerFactory.getLogger(Bot.class);

    public static AudioTrackManager trackManager;
    private final JDA jda;
    private final CommandManager commandManager;

    @Inject
    public Bot(JDA jda, CommandManager commandManager) throws InterruptedException, ExecutionException, TimeoutException {
        this.jda = jda;
        this.commandManager = commandManager;
    }

    public void init() {
        startUpMessages();
        jda.getPresence().setActivity(Activity.playing(" in " + jda.getGuilds().size() + " guilds"));
        LOG.info("Started and ready with bot user {}", jda.getSelfUser().getName());
        trackManager = new AudioTrackManager();
    }

    private void startUpMessages() {
        List<Command> descriptionLessCommands = new ArrayList<>();

        for (var c : commandManager.getCommands()) {
            LOG.info("Adding {}", c.getClass().getSimpleName());
            if (c.getDescription() == null) {
                descriptionLessCommands.add(c);
            }
        }
        LOG.info(
                "Added {} commands to command manager",
                commandManager.getCommands().size());

        if (!descriptionLessCommands.isEmpty()) {
            for (var c : descriptionLessCommands) {
                LOG.warn(
                        "The following command does not have a description: {}",
                        c.getClass().getSimpleName());
            }
        }
    }
}
