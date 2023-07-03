package me.afarrukh.hashbot.utils;

import com.google.inject.Guice;
import me.afarrukh.hashbot.config.Config;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import net.dv8tion.jda.api.entities.User;

public class UserUtils {

    public static boolean isBotAdmin(User u) {
        var injector = Guice.createInjector(new CoreBotModule());
        return injector.getInstance(Config.class).getOwnerIds().contains(u.getId());
    }
}
