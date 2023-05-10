package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.api.entities.User;

public class UserUtils {

    public static boolean isBotAdmin(User u) {
        return Bot.getConfig().getOwnerIds().contains(u.getId());
    }
}
