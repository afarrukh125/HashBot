package me.afarrukh.hashbot.core;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.afarrukh.hashbot.cli.CommandLineInputManager;
import me.afarrukh.hashbot.config.Config;
import me.afarrukh.hashbot.data.Database;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

@Singleton
public class Bot {
    private static final Logger LOG = LoggerFactory.getLogger(Bot.class);
    private CommandManager commandManager;
    private JDA botUser;
    ReactionManager reactionManager;
    private final Config config;
    private final AudioTrackManager audioTrackManager;
    private final MessageListener messageListener;
    private final Database database;
    private CommandLineInputManager commandLineInputManager;

    private static boolean initialised;

    @Inject
    public Bot(
            Config config,
            CommandManager commandManager,
            AudioTrackManager audioTrackManager,
            ReactionManager reactionManager,
            CommandLineInputManager commandLineInputManager,
            JDA botUser,
            MessageListener messageListener,
            Database database)
            throws InterruptedException, ExecutionException, TimeoutException {
        this.config = config;
        this.commandManager = commandManager;
        this.audioTrackManager = audioTrackManager;
        this.reactionManager = reactionManager;
        this.commandLineInputManager = commandLineInputManager;
        this.botUser = botUser;
        this.messageListener = messageListener;
        this.database = database;
    }

    public JDA getBotUser() {
        return botUser;
    }

    public Config getConfig() {
        return config;
    }

    public void init() {
        if (initialised) {
            throw new IllegalStateException("Bot has already been initialised");
        }

        botUser.addEventListener(messageListener);
        botUser.getPresence()
                .setActivity(Activity.playing(" in " + botUser.getGuilds().size() + " guilds"));
        LOG.info("Started and ready with bot user {}", botUser.getSelfUser().getName());

        try (ExecutorService cliExecutor = newSingleThreadExecutor()) {
            cliExecutor.execute(() -> {
                while (true) {
                    Scanner scanner = new Scanner(System.in);
                    String input = scanner.nextLine();
                    commandLineInputManager.processInput(input);
                }
            });
        }
        initialised = true;
    }

    public AudioTrackManager getTrackManager() {
        return audioTrackManager;
    }
}
