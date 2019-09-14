package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.econ.FlipCommand;
import me.afarrukh.hashbot.commands.econ.GiveCommand;
import me.afarrukh.hashbot.commands.extras.UrbanDictionaryCommand;
import me.afarrukh.hashbot.commands.management.bot.*;
import me.afarrukh.hashbot.commands.management.bot.owner.SetNameCommand;
import me.afarrukh.hashbot.commands.management.guild.*;
import me.afarrukh.hashbot.commands.management.guild.roles.*;
import me.afarrukh.hashbot.commands.management.user.*;
import me.afarrukh.hashbot.commands.music.*;
import me.afarrukh.hashbot.commands.music.playlist.DeleteListCommand;
import me.afarrukh.hashbot.commands.music.playlist.LoadListCommand;
import me.afarrukh.hashbot.commands.music.playlist.SavePlaylistCommand;
import me.afarrukh.hashbot.commands.music.playlist.ViewListCommand;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.commands.tagging.SystemCommand;
import me.afarrukh.hashbot.commands.tagging.ViewCategoriesCommand;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.cli.CommandLineInputManager;
import me.afarrukh.hashbot.data.SQLUserDataManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;

public class Bot {
    public static CommandManager commandManager;
    public static GameRoleManager gameRoleManager;
    public static MusicManager musicManager;
    public static JDA botUser;
    static ReactionManager reactionManager;
    private final String token;
    private CommandLineInputManager ownerInputManager;

    /**
     * Creates our JDA user
     *
     * @param token The unique token used to login to the discord servers
     */
    public Bot(String token) {
        this.token = token;
        try {
            init();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the commands and initialises all the managers
     *
     * @throws LoginException The login servers failed
     */
    private void init() throws LoginException, InterruptedException {
        botUser = new JDABuilder(AccountType.BOT)
                .setToken(token)
                .addEventListener(new MessageListener())
                .build().awaitReady();

        commandManager = new CommandManager()
                .addCommand(new HelpCommand())
                .addCommand(new UptimeCommand())
                .addCommand(new AddRoleCommand())
                .addCommand(new CreateRoleCommand())
                .addCommand(new RemoveRoleCommand())
                .addCommand(new SetPrefixCommand())
                .addCommand(new ColourChangeCommand())
                .addCommand(new SetNickCommand())
                .addCommand(new RoleRGBCommand())
                .addCommand(new ListMembersCommand())
                .addCommand(new DeleteRoleCommand())
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
                .addCommand(new LoopCommand())
                .addCommand(new LoopQueueCommand())
                .addCommand(new GiveCommand())
                .addCommand(new FlipCommand())
                .addCommand(new NowPlayingCommand())
                .addCommand(new PauseCommand())
                .addCommand(new ResumeCommand())
                .addCommand(new ResetPlayerCommand())
                .addCommand(new SetVolumeCommand())
                .addCommand(new SetPinThresholdCommand())
                .addCommand(new PlayTopCommand())
                .addCommand(new SeekCommand())
                .addCommand(new SetPinnedChannel())
                .addCommand(new SetUnpinnedCommand())
                .addCommand(new MoveCommand())
                .addCommand(new TimeCreatedCommand())
                .addCommand(new ShuffleCommand())
                .addCommand(new FairShuffleCommand())
                .addCommand(new InterleaveCommand())
                .addCommand(new SetRoleCommand())
                .addCommand(new FairPlayCommand())
                .addCommand(new ViewCategoriesCommand())
                .addCommand(new UrbanDictionaryCommand())
                .addCommand(new CheckMemoryCommand())
                .addCommand(new RoleStatsCommand())
                .addCommand(new SkipCommand());

        //setMusicOnly();

        musicManager = new MusicManager();

        gameRoleManager = new GameRoleManager();
        reactionManager = new ReactionManager();

        Timer experienceTimer = new Timer();
        experienceTimer.schedule(new VoiceExperienceTimer(), Constants.EXPERIENCE_TIMER * 1000, Constants.EXPERIENCE_TIMER * 1000);

        startUpMessages();

        botUser.getPresence().setGame(Game.playing(" in " + botUser.getGuilds().size() + " guilds"));
        System.out.println("\nStarted and ready with bot user " + botUser.getSelfUser().getName());

        setupNames();

        ownerInputManager = new CommandLineInputManager();

        new Thread(() -> {
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String input = scanner.next();
                ownerInputManager.processInput(input);
            }
        }
        ).start();
    }

    /**
     * Removes all commands that are not music related commands.
     */
    private void setMusicOnly() {
        for (Command c : commandManager.getCommandList()) {
            if (!(c instanceof MusicCommand) && !(c instanceof SystemCommand) && !(c instanceof PruneCommand) && !(c instanceof SetNameCommand)
                    && !(c instanceof SetNickCommand) && !(c instanceof SetPrefixCommand) && !(c instanceof HelpCommand) && !(c instanceof ViewCategoriesCommand))
                commandManager.removeCommand(c);
        }
    }

    private void startUpMessages() {
        List<Command> descriptionLessCommands = new ArrayList<>();

        for (Command c : commandManager.getCommandList()) {
            System.out.println("Adding " + c.getClass().getSimpleName());
            if (c.getDescription() == null)
                descriptionLessCommands.add(c);
        }
        System.out.println("Added " + commandManager.getCommandList().size() + " commands to command manager.");

        if (!descriptionLessCommands.isEmpty()) {
            System.out.println("\nThe following commands do not have descriptions: ");
            for (Command c : descriptionLessCommands) {
                System.out.println(c.getClass().getSimpleName());
            }
        }
    }

    private void setupNames() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLUserDataManager userDataManager = new SQLUserDataManager(botUser.getGuilds().get(0).getMemberById(botUser.getSelfUser().getId()));
                for (Guild g : botUser.getGuilds()) {
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
        }).start();
    }
}
