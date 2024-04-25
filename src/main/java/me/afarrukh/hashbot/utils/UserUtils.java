package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.config.Config;
import net.dv8tion.jda.api.entities.User;

public class UserUtils {

    public static boolean isBotAdmin(User u, Config config) {
        return config.getOwnerIds().contains(u.getId());
    }
}
