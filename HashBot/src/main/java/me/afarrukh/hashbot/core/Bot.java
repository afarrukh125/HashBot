package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.econ.FlipCommand;
import me.afarrukh.hashbot.commands.econ.GiveCommand;
import me.afarrukh.hashbot.commands.extras.UrbanDictionaryCommand;
import me.afarrukh.hashbot.commands.extras.fortnite.FortniteRegisterCommand;
import me.afarrukh.hashbot.commands.extras.fortnite.FortniteUnregisterCommand;
import me.afarrukh.hashbot.commands.extras.fortnite.SetFortniteChannelCommand;
import me.afarrukh.hashbot.commands.extras.fortnite.UnsetFortniteChannelCommand;
import me.afarrukh.hashbot.commands.management.bot.*;
import me.afarrukh.hashbot.commands.management.bot.owner.SetNameCommand;
import me.afarrukh.hashbot.commands.management.guild.RoleRGBCommand;
import me.afarrukh.hashbot.commands.management.guild.SetPinnedChannel;
import me.afarrukh.hashbot.commands.management.guild.SetPrefixCommand;
import me.afarrukh.hashbot.commands.management.guild.SetUnpinned;
import me.afarrukh.hashbot.commands.management.guild.roles.CreateRoleCommand;
import me.afarrukh.hashbot.commands.management.guild.roles.DeleteRoleCommand;
import me.afarrukh.hashbot.commands.management.user.*;
import me.afarrukh.hashbot.commands.music.*;
import me.afarrukh.hashbot.commands.tagging.ViewCategoriesCommand;
import me.afarrukh.hashbot.extras.ExtrasManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class Bot {
    private final String token;

    public static CommandManager commandManager;
    static ReactionManager reactionManager;
    public static GameRoleManager gameRoleManager;
    public static MusicManager musicManager;
    public static ExtrasManager extrasManager;

    public static JDA botUser;

    /**
     * Creates our JDA user
     * @param token The unique token used to login to the discord servers
     */
    public Bot(String token) {
        this.token = token;
        try {
            init();
        } catch (LoginException | InterruptedException e) { e.printStackTrace(); }
    }

    /**
     * Adds the commands and initialises all the managers
     * @throws LoginException The login servers failed
     * @throws InterruptedException The connection was interrupted
     */
    private void init() throws LoginException, InterruptedException {
        botUser = new JDABuilder(AccountType.BOT)
                .setToken(token)
                .addEventListener(new MessageListener())
                .buildBlocking();

        commandManager = new CommandManager()
                .addCommand(new HelpCommand())
                .addCommand(new UptimeCommand())
                .addCommand(new AddRoleCommand())
                .addCommand(new CreateRoleCommand())
                .addCommand(new RemoveRoleCommand())
                .addCommand(new SetPinnedChannel())
                .addCommand(new SetUnpinned())
                .addCommand(new SetPrefixCommand())
                .addCommand(new ColourChangeCommand())
                .addCommand(new SetNickCommand())
                .addCommand(new RoleRGBCommand())
                .addCommand(new DeleteRoleCommand())
                .addCommand(new SetNameCommand())
                .addCommand(new PingCommand())
                .addCommand(new RewardCommand())
                .addCommand(new StatsCommand())
                .addCommand(new PruneCommand())
                .addCommand(new LeaderboardCommand())
                .addCommand(new PlayCommand())
                .addCommand(new QueueCommand())
                .addCommand(new RemoveCommand())
                .addCommand(new ClearQueueCommand())
                .addCommand(new PruneQueueCommand())
                .addCommand(new DisconnectCommand())
                .addCommand(new LoopCommand())
                .addCommand(new GiveCommand())
                .addCommand(new FlipCommand())
                .addCommand(new NowPlayingCommand())
                .addCommand(new PauseCommand())
                .addCommand(new ResumeCommand())

                .addCommand(new FortniteRegisterCommand())
                .addCommand(new SetFortniteChannelCommand())
                .addCommand(new FortniteUnregisterCommand())
                .addCommand(new UnsetFortniteChannelCommand())

                .addCommand(new PlayTopCommand())
                .addCommand(new SeekCommand())
                .addCommand(new MoveCommand())
                .addCommand(new ShuffleCommand())
                .addCommand(new ViewCategoriesCommand())
                .addCommand(new UrbanDictionaryCommand())
                .addCommand(new CheckMemoryCommand())
                .addCommand(new SkipCommand());

        startUpMessages();

        musicManager = new MusicManager();

        gameRoleManager = new GameRoleManager();
        reactionManager = new ReactionManager();

        botUser.getPresence().setGame(Game.playing(" in " + botUser.getGuilds().size() + " guilds"));

        extrasManager = new ExtrasManager();

        botUser.addEventListener(extrasManager);
    }

    private void startUpMessages() {
        List<Command> descriptionLessCommands = new ArrayList<>();

        for(Command c: commandManager.getCommandList()) {
            System.out.println("Adding " + c.getClass().getSimpleName());
            if(c.getDescription() == null)
                descriptionLessCommands.add(c);
        }
        System.out.println("Added " +commandManager.getCommandList().size()+ " commands to command manager.");

        if(!descriptionLessCommands.isEmpty()) {
            System.out.println("\nThe following commands do not have descriptions: ");
            for (Command c : descriptionLessCommands) {
                System.out.println(c.getClass().getSimpleName());
            }
        }
        System.out.println("\nStarted and ready with bot user " + botUser.getSelfUser().getName());
    }
}
