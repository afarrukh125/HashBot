package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.commands.management.bot.PingCommand;
import me.afarrukh.hashbot.commands.management.user.ColourChangeCommand;
import me.afarrukh.hashbot.commands.management.user.RewardCommand;
import me.afarrukh.hashbot.commands.management.user.StatsCommand;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;

public class Bot {
    private static JDA botUser;
    private String token;

    static CommandManager commandManager;

    public Bot(String token) {
        this.token = token;
        try {
            init();
        } catch (LoginException | InterruptedException e) { e.printStackTrace(); }
    }

    public void init() throws LoginException, InterruptedException {
        botUser = new JDABuilder(AccountType.BOT)
                .setToken(token)
                .addEventListener(new MessageListener())
                .buildBlocking();

        commandManager = new CommandManager()
                .addCommand(new ColourChangeCommand())
                .addCommand(new PingCommand())
                .addCommand(new RewardCommand())
                .addCommand(new StatsCommand());
    }
}
