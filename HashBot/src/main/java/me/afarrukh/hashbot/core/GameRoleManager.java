package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.gameroles.GuildGameRoleManager;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class GameRoleManager {

    Map<Long, GuildGameRoleManager> gameRoleManagers;

    public GameRoleManager() {
        this.gameRoleManagers = new HashMap<>();
    }

    public synchronized GuildGameRoleManager getGuildRoleManager(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildGameRoleManager roleManager = gameRoleManagers.get(guildId); //Gets the current role manager for this guild

        if(roleManager == null) { // If the guild doesn't already have a role manager then create one
            roleManager = new GuildGameRoleManager(guild);
            gameRoleManagers.put(guildId, roleManager);
        }

        return roleManager;
    }
}
