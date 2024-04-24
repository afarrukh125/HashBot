package me.afarrukh.hashbot;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import me.afarrukh.hashbot.cli.CommandLineInputManager;
import net.dv8tion.jda.api.JDA;

public class CommandLineInputManagerModule extends AbstractModule {

    @Provides
    @Singleton
    public CommandLineInputManager commandLineInputManager(JDA jda) {
        return new CommandLineInputManager(jda);
    }
}
