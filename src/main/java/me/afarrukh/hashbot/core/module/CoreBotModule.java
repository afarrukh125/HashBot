package me.afarrukh.hashbot.core.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.*;
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
import me.afarrukh.hashbot.core.*;
import me.afarrukh.hashbot.data.Database;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@Singleton
public class CoreBotModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(CoreBotModule.class);

    private static boolean instantiated;
    public CoreBotModule() {
        if(instantiated) {
            throw new IllegalStateException("Already instantiated!");
        }
        instantiated = true;
    }

    @Override
    protected void configure() {
        var config = getConfigFromFile();
        var database = Database.create(config);
        AudioTrackManager audioTrackManager = new AudioTrackManager();
        bind(AudioTrackManager.class).toInstance(audioTrackManager);
        var commandManager = loadCommands(database, audioTrackManager);
        bind(MessageListener.class).in(Scopes.SINGLETON);
        bind(CommandManager.class).toInstance(commandManager);
        bind(Config.class).toInstance(config);
        bind(ReactionManager.class).in(Scopes.SINGLETON);
        bind(CommandLineInputManager.class).in(Scopes.SINGLETON);
        bind(JDA.class).toInstance(getJdaInstance(config, commandManager));
        bind(Bot.class).asEagerSingleton();
    }

    @Singleton
    private CommandManager loadCommands(Database database, AudioTrackManager audioTrackManager) {
        return new CommandManager.Builder()
                .addCommand(new CheckMemoryCommand(database))
                .addCommand(new ClearCommand(database))
                .addCommand(new ClearQueueCommand(database))
                .addCommand(new CommandListCommand(database))
                .addCommand(new DeleteListCommand(database))
                .addCommand(new DisconnectCommand(database, audioTrackManager))
                .addCommand(new FairPlayCommand(database))
                .addCommand(new FairShuffleCommand(database))
                .addCommand(new HelpCommand(database))
                .addCommand(new InterleaveCommand(database))
                .addCommand(new LoadListCommand(database))
                .addCommand(new LoopCommand(database))
                .addCommand(new LoopQueueCommand(database))
                .addCommand(new MoveCommand(database))
                .addCommand(new NowPlayingCommand(database))
                .addCommand(new PauseCommand(database))
                .addCommand(new PingCommand(database))
                .addCommand(new PlayCommand(database))
                .addCommand(new PlayTopCommand(database))
                .addCommand(new PruneCommand(database))
                .addCommand(new PruneQueueCommand(database))
                .addCommand(new QueueCommand(database))
                .addCommand(new RemoveCommand(database, audioTrackManager))
                .addCommand(new RemoveRangeCommand(database))
                .addCommand(new ResetPlayerCommand(database, audioTrackManager))
                .addCommand(new ResumeCommand(database))
                .addCommand(new ReverseQueueCommand(database))
                .addCommand(new RoleRGBCommand(database))
                .addCommand(new SavePlaylistCommand(database))
                .addCommand(new SeekCommand(database))
                .addCommand(new SetNameCommand(database))
                .addCommand(new SetPinThresholdCommand(database))
                .addCommand(new SetPinnedChannelCommand(database))
                .addCommand(new SetPrefixCommand(database))
                .addCommand(new SetUnpinnedCommand(database))
                .addCommand(new SetVolumeCommand(database))
                .addCommand(new ShuffleCommand(database))
                .addCommand(new SkipCommand(database))
                .addCommand(new SortByLengthCommand(database))
                .addCommand(new UptimeCommand(database))
                .addCommand(new ViewListCommand(database))
                .build();
    }

    @Singleton
    private static Config getConfigFromFile() {
        try {
            var mapper = new ObjectMapper();
            var targetFile = new File("settings.json");
            if (targetFile.exists()) {
                return mapper.readValue(targetFile, Config.class);
            } else {
                var tempConfig = new Config(
                        "!",
                        "TOKEN_HERE",
                        singletonList("111608457290895360"),
                        "OPTIONAL_NEO4J_DB_URI_HERE(neo4j+s://...)",
                        "OPTIONAL_NEO4J_DB_USERNAME_HERE",
                        "OPTIONAL_NEO4J_DB_PASSWORD_HERE");
                mapper.writerWithDefaultPrettyPrinter().writeValue(targetFile, tempConfig);
                LOG.info(
                        "Config did not exist, it has been created in {}, please fill it out and rerun.",
                        targetFile.getAbsolutePath());
                System.exit(0);
                return null;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Singleton
    private JDA getJdaInstance(Config config, CommandManager commandManager) {
        JDA jda = null;
        try {
            jda = JDABuilder.create(
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
            startUpMessages(commandManager);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return jda;
    }

    private void startUpMessages(CommandManager commandManager) {
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
