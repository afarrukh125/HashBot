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

public class CoreBotModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(CoreBotModule.class);

    @Override
    protected void configure() {
        bind(MessageListener.class).in(Scopes.SINGLETON);
        bind(CommandManager.class).toInstance(loadCommands());
        bind(Config.class).toInstance(getConfigFromFile());
        bind(AudioTrackManager.class).in(Scopes.SINGLETON);
        bind(ReactionManager.class).in(Scopes.SINGLETON);
        bind(CommandLineInputManager.class).in(Scopes.SINGLETON);
        bind(JDA.class).toInstance(getJdaInstance());
        bind(Bot.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    private CommandManager loadCommands() {
        return new CommandManager.Builder()
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

    @Provides
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

    @Inject
    @Provides
    @Singleton
    private JDA getJdaInstance() {
        var injector = Guice.createInjector(new CoreBotModule());
        var config = injector.getInstance(Config.class);
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
            startUpMessages();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return jda;
    }

    @Inject
    private void startUpMessages() {
        var injector = Guice.createInjector(new CoreBotModule());
        var commandManager = injector.getInstance(CommandManager.class);
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
