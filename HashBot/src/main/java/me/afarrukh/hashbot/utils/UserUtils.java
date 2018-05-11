package me.afarrukh.hashbot.utils;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

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
}
