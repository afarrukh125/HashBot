package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.commands.management.bot.HelpCommand;
import me.afarrukh.hashbot.commands.management.bot.PingCommand;
import me.afarrukh.hashbot.commands.management.bot.SetNickCommand;
import me.afarrukh.hashbot.commands.management.bot.owner.SetNameCommand;
import me.afarrukh.hashbot.commands.management.guild.RoleRGBCommand;
import me.afarrukh.hashbot.commands.management.guild.SetPinnedChannel;
import me.afarrukh.hashbot.commands.management.guild.SetPrefixCommand;
import me.afarrukh.hashbot.commands.management.guild.SetUnpinned;
import me.afarrukh.hashbot.commands.management.user.AddRoleCommand;
import me.afarrukh.hashbot.commands.management.guild.roles.CreateRoleCommand;
import me.afarrukh.hashbot.commands.management.user.*;
import me.afarrukh.hashbot.commands.music.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;

public class Bot {
    private final String token;

    public static CommandManager commandManager;
    static ReactionManager reactionManager;
    public static GameRoleManager gameRoleManager;
    public static MusicManager musicManager;

    public Bot(String token) {
        this.token = token;
        try {
            init();
        } catch (LoginException | InterruptedException e) { e.printStackTrace(); }
    }

    private void init() throws LoginException, InterruptedException {
        JDA botUser = new JDABuilder(AccountType.BOT)
                .setToken(token)
                .addEventListener(new MessageListener())
                .buildBlocking();

        commandManager = new CommandManager()
                .addCommand(new HelpCommand())
                .addCommand(new AddRoleCommand())
                .addCommand(new CreateRoleCommand())
                .addCommand(new RemoveRoleCommand())
                .addCommand(new SetPinnedChannel())
                .addCommand(new SetUnpinned())
                .addCommand(new SetPrefixCommand())
                .addCommand(new ColourChangeCommand())
                .addCommand(new SetNickCommand())
                .addCommand(new RoleRGBCommand())
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
                .addCommand(new PlaySkipCommand())
                .addCommand(new NowPlayingCommand())
                .addCommand(new PauseCommand())
                .addCommand(new ResumeCommand())
                .addCommand(new PlayTopCommand())
                .addCommand(new SeekCommand())
                .addCommand(new MoveCommand())
                .addCommand(new ShuffleCommand())
                .addCommand(new SkipCommand());

        musicManager = new MusicManager();
        gameRoleManager = new GameRoleManager();
        reactionManager = new ReactionManager();
    }
}
