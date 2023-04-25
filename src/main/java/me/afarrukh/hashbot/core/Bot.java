package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.cli.CommandLineInputManager;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.audiotracks.*;
import me.afarrukh.hashbot.commands.audiotracks.playlist.DeleteListCommand;
import me.afarrukh.hashbot.commands.audiotracks.playlist.LoadListCommand;
import me.afarrukh.hashbot.commands.audiotracks.playlist.SavePlaylistCommand;
import me.afarrukh.hashbot.commands.audiotracks.playlist.ViewListCommand;
import me.afarrukh.hashbot.commands.econ.FlipCommand;
import me.afarrukh.hashbot.commands.econ.GiveCommand;
import me.afarrukh.hashbot.commands.extras.UrbanDictionaryCommand;
import me.afarrukh.hashbot.commands.management.bot.*;
import me.afarrukh.hashbot.commands.management.bot.owner.SetNameCommand;
import me.afarrukh.hashbot.commands.management.guild.*;
import me.afarrukh.hashbot.commands.management.guild.roles.ListMembersCommand;
import me.afarrukh.hashbot.commands.management.guild.roles.RoleStatsCommand;
import me.afarrukh.hashbot.commands.management.user.*;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.SQLUserDataManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Bot {
    public static CommandManager commandManager;
    public static GameRoleManager gameRoleManager;
    public static AudioTrackManager trackManager;
    private static JDA botUser;
    static ReactionManager reactionManager;
    private final String token;
    private CommandLineInputManager ownerInputManager;

    /**
     * Creates our JDA user
     *
     * @param token The unique token used to log in to the discord servers
     */
    public Bot(String token) throws InterruptedException {
        this.token = token;
        init();
    }

    public static JDA botUser() {
        return botUser;
    }

    /**
     * Adds the commands and initialises all the managers
     */
    private void init() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.execute(this::initialiseBotUser);
        executor.execute(this::loadCommands);
        executor.shutdown();

        //noinspection ResultOfMethodCallIgnored
        executor.awaitTermination(1, TimeUnit.MINUTES);


        botUser().addEventListener(new MessageListener());
        botUser().getPresence().setActivity(Activity.playing(" in " + botUser().getGuilds().size() + " guilds"));
        System.out.println("\n" + new Date(System.currentTimeMillis()) + ": Started and ready with bot user " + botUser().getSelfUser().getName());

        trackManager = new AudioTrackManager();

        gameRoleManager = new GameRoleManager();
        reactionManager = new ReactionManager();

        setupNames();

        ownerInputManager = new CommandLineInputManager();

        new Thread(() -> {
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                ownerInputManager.processInput(input);
            }
        }
        ).start();
    }

    private void startUpMessages() {
        List<Command> descriptionLessCommands = new ArrayList<>();

        for (Command c : commandManager.getCommands()) {
            System.out.println("Adding " + c.getClass().getSimpleName());
            if (c.getDescription() == null)
                descriptionLessCommands.add(c);
        }
        System.out.println("Added " + commandManager.getCommands().size() + " commands to command manager.");

        if (!descriptionLessCommands.isEmpty()) {
            System.out.println("\nThe following commands do not have descriptions: ");
            for (Command c : descriptionLessCommands)
                System.out.println(c.getClass().getSimpleName());

        }
    }

    private void initialiseBotUser() {
        try {
            botUser = JDABuilder.create(token, GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.GUILD_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_MESSAGES)
                    .disableCache(CacheFlag.ACTIVITY,
                            CacheFlag.EMOJI,
                            CacheFlag.CLIENT_STATUS)
                    .build()
                    .awaitReady();


            Timer experienceTimer = new Timer();
            experienceTimer.schedule(new VoiceExperienceTimer(), Constants.VOICE_EXPERIENCE_TIMER * 1000, Constants.VOICE_EXPERIENCE_TIMER * 1000);

            startUpMessages();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void loadCommands() {
        commandManager = new CommandManager.Builder()
                .addCommand(new CommandListCommand())
                .addCommand(new UptimeCommand())
                .addCommand(new SetPrefixCommand())
                .addCommand(new ColourChangeCommand())
                .addCommand(new SetNickCommand())
                .addCommand(new RoleRGBCommand())
                .addCommand(new ListMembersCommand())
                .addCommand(new SetNameCommand())
                .addCommand(new PingCommand())
                .addCommand(new RewardCommand())
                .addCommand(new StatsCommand())
                .addCommand(new PruneCommand())
                .addCommand(new LeaderboardCommand())
                .addCommand(new PlayCommand())
                .addCommand(new QueueCommand())
                .addCommand(new SavePlaylistCommand())
                .addCommand(new LoadListCommand())
                .addCommand(new ViewListCommand())
                .addCommand(new DeleteListCommand())
                .addCommand(new RemoveCommand())
                .addCommand(new ClearQueueCommand())
                .addCommand(new PruneQueueCommand())
                .addCommand(new DisconnectCommand())
                .addCommand(new ClearCommand())
                .addCommand(new SortByLengthCommand())
                .addCommand(new LoopCommand())
                .addCommand(new LoopQueueCommand())
                .addCommand(new GiveCommand())
                .addCommand(new FlipCommand())
                .addCommand(new NowPlayingCommand())
                .addCommand(new RemoveRangeCommand())
                .addCommand(new PauseCommand())
                .addCommand(new ResumeCommand())
                .addCommand(new ResetPlayerCommand())
                .addCommand(new SetVolumeCommand())
                .addCommand(new HelpCommand())
                .addCommand(new SetPinThresholdCommand())
                .addCommand(new PlayTopCommand())
                .addCommand(new SeekCommand())
                .addCommand(new SetPinnedChannel())
                .addCommand(new SetUnpinnedCommand())
                .addCommand(new MoveCommand())
                .addCommand(new TimeCreatedCommand())
                .addCommand(new ReverseQueueCommand())
                .addCommand(new ShuffleCommand())
                .addCommand(new FairShuffleCommand())
                .addCommand(new InterleaveCommand())
                .addCommand(new FairPlayCommand())
                .addCommand(new UrbanDictionaryCommand())
                .addCommand(new CheckMemoryCommand())
                .addCommand(new RoleStatsCommand())
                .addCommand(new SkipCommand())
                .build();

    }

    private void setupNames() {
        for (Guild g : botUser().getGuilds()) {
            try {
                SQLUserDataManager.updateUsernames(g);
                for (Member m : g.getMembers()) {
                    if (!SQLUserDataManager.getUserData(m).next())
                        SQLUserDataManager.addMember(m);
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
