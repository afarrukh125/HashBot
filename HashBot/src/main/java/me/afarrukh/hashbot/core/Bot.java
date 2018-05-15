package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.commands.management.bot.PingCommand;
import me.afarrukh.hashbot.commands.management.bot.SetNickCommand;
import me.afarrukh.hashbot.commands.management.bot.owner.SetNameCommand;
import me.afarrukh.hashbot.commands.management.user.*;
import me.afarrukh.hashbot.commands.music.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;

public class Bot {
    private static JDA botUser;
    private String token;

    static CommandManager commandManager;
    public static MusicManager musicManager;

    public Bot(String token) {
        this.token = token;
        try {
            init();
        } catch (LoginException | InterruptedException e) { e.printStackTrace(); }
    }

    private void init() throws LoginException, InterruptedException {
        botUser = new JDABuilder(AccountType.BOT)
                .setToken(token)
                .addEventListener(new MessageListener())
                .buildBlocking();

        commandManager = new CommandManager()
                .addCommand(new ColourChangeCommand())
                .addCommand(new SetNameCommand())
                .addCommand(new PingCommand())
                .addCommand(new RewardCommand())
                .addCommand(new StatsCommand())
                .addCommand(new PruneCommand())
                .addCommand(new PlayCommand())
                .addCommand(new LeaderboardCommand())
                .addCommand(new QueueCommand())
                .addCommand(new RemoveCommand())
                .addCommand(new ClearQueueCommand())
                .addCommand(new DisconnectCommand())
                .addCommand(new LoopCommand())
                .addCommand(new PlaySkipCommand())
                .addCommand(new NowPlayingCommand())
                .addCommand(new PauseCommand())
                .addCommand(new PlayTopCommand())
                .addCommand(new SeekCommand())
                .addCommand(new AddRoleCommand())
                .addCommand(new MoveCommand())
                .addCommand(new SetNickCommand())
                .addCommand(new ShuffleCommand())
                .addCommand(new SkipCommand());

        musicManager = new MusicManager();
    }
}
