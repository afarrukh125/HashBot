package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.config.Constants;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

public class UserUtils {

    public static int getHighestRolePosition(Member m) {
        int highest = 0;

        for(Role r: m.getRoles()) {
            if(r.getPosition() > highest) {
                highest = r.getPosition();
            }
        }
        return highest;
    }

    public static boolean isBotAdmin(User u) {
        if(u.getId().equals(Constants.OWNER_ID))
            return true;
        return false;
    }
}
