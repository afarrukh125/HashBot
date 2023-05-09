package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.cli.CommandLineInputManager;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.audiotracks.*;
import me.afarrukh.hashbot.commands.audiotracks.playlist.DeleteListCommand;
import me.afarrukh.hashbot.commands.audiotracks.playlist.LoadListCommand;
import me.afarrukh.hashbot.commands.audiotracks.playlist.SavePlaylistCommand;
import me.afarrukh.hashbot.commands.audiotracks.playlist.ViewListCommand;
import me.afarrukh.hashbot.commands.management.bot.*;
import me.afarrukh.hashbot.commands.management.bot.owner.SetNameCommand;
import me.afarrukh.hashbot.commands.management.guild.*;
import me.afarrukh.hashbot.commands.management.user.ClearCommand;
import me.afarrukh.hashbot.commands.management.user.PruneCommand;
import me.afarrukh.hashbot.config.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class Bot {
    private static final Logger LOG = LoggerFactory.getLogger(Bot.class);
    public static CommandManager commandManager;
    public static AudioTrackManager trackManager;
    private static JDA botUser;
    static ReactionManager reactionManager;
    private static Config config;
    private CommandLineInputManager commandLineInputManager;

    public Bot(Config config) throws InterruptedException {
        Bot.config = config;
        init();
    }

    public static JDA botUser() {
        return botUser;
    }

    public static Config getConfig() {
        return config;
    }

    /**
     * Adds the commands and initialises all the managers
     */
    private void init() throws InterruptedException {
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {

            executor.execute(this::initialiseBotUser);
            executor.execute(this::loadCommands);
            executor.shutdown();

            //noinspection ResultOfMethodCallIgnored
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }

        botUser().addEventListener(new MessageListener());
        botUser()
                .getPresence()
                .setActivity(Activity.playing(" in " + botUser().getGuilds().size() + " guilds"));
        LOG.info("Started and ready with bot user {}", botUser().getSelfUser().getName());
        trackManager = new AudioTrackManager();

        reactionManager = new ReactionManager();
        commandLineInputManager = new CommandLineInputManager();

        try (ExecutorService cliExecutor = newSingleThreadExecutor()) {
            cliExecutor.execute(() -> {
                while (true) {
                    Scanner scanner = new Scanner(System.in);
                    String input = scanner.nextLine();
                    commandLineInputManager.processInput(input);
                }
            });
        }
    }

    private void startUpMessages() {
        List<Command> descriptionLessCommands = new ArrayList<>();

        for (Command c : commandManager.getCommands()) {
            LOG.info("Adding {}", c.getClass().getSimpleName());
            if (c.getDescription() == null) {
                descriptionLessCommands.add(c);
            }
        }
        LOG.info(
                "Added {} commands to command manager",
                commandManager.getCommands().size());

        if (!descriptionLessCommands.isEmpty()) {
            for (Command c : descriptionLessCommands) {
                LOG.warn(
                        "The following command does not have a description: {}",
                        c.getClass().getSimpleName());
            }
        }
    }

    private void initialiseBotUser() {
        try {
            botUser = JDABuilder.create(
                            config.getBotToken(),
                            GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.MESSAGE_CONTENT)
                    .disableCache(
                            CacheFlag.ACTIVITY,
                            CacheFlag.CLIENT_STATUS,
                            CacheFlag.EMOJI,
                            CacheFlag.SCHEDULED_EVENTS,
                            CacheFlag.STICKER)
                    .build()
                    .awaitReady();
            startUpMessages();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void loadCommands() {
        commandManager = new CommandManager.Builder()
                .addCommand(new CheckMemoryCommand())
                .addCommand(new ClearCommand())
                .addCommand(new ClearQueueCommand())
                .addCommand(new CommandListCommand())
                .addCommand(new DeleteListCommand())
                .addCommand(new DisconnectCommand())
                .addCommand(new FairPlayCommand())
                .addCommand(new FairShuffleCommand())
                .addCommand(new HelpCommand())
                .addCommand(new InterleaveCommand())
                .addCommand(new LoadListCommand())
                .addCommand(new LoopCommand())
                .addCommand(new LoopQueueCommand())
                .addCommand(new MoveCommand())
                .addCommand(new NowPlayingCommand())
                .addCommand(new PauseCommand())
                .addCommand(new PingCommand())
                .addCommand(new PlayCommand())
                .addCommand(new PlayTopCommand())
                .addCommand(new PruneCommand())
                .addCommand(new PruneQueueCommand())
                .addCommand(new QueueCommand())
                .addCommand(new RemoveCommand())
                .addCommand(new RemoveRangeCommand())
                .addCommand(new ResetPlayerCommand())
                .addCommand(new ResumeCommand())
                .addCommand(new ReverseQueueCommand())
                .addCommand(new RoleRGBCommand())
                .addCommand(new SavePlaylistCommand())
                .addCommand(new SeekCommand())
                .addCommand(new SetNameCommand())
                .addCommand(new SetPinThresholdCommand())
                .addCommand(new SetPinnedChannel())
                .addCommand(new SetPrefixCommand())
                .addCommand(new SetUnpinnedCommand())
                .addCommand(new SetVolumeCommand())
                .addCommand(new ShuffleCommand())
                .addCommand(new SkipCommand())
                .addCommand(new SortByLengthCommand())
                .addCommand(new UptimeCommand())
                .addCommand(new ViewListCommand())
                .build();
    }
}
