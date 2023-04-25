package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.config.Constants;
import net.dv8tion.jda.api.entities.User;

public class UserUtils {

    public static boolean isBotAdmin(User u) {
        return Constants.ownerIds.contains(u.getId());
    }
}
