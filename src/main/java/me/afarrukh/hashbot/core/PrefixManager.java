package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.prefixes.GuildPrefixManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class PrefixManager {

    private final Map<Long, GuildPrefixManager> guildPrefixManagers;

    public PrefixManager() {
        this.guildPrefixManagers = new HashMap<>();

        for (Guild guild : Bot.botUser().getGuilds()) {
            getGuildRoleManager(guild);
        }
    }

    public GuildPrefixManager getGuildRoleManager(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildPrefixManager roleManager = guildPrefixManagers.get(guildId);

        if (roleManager == null) {
            roleManager = new GuildPrefixManager(guild);
            guildPrefixManagers.put(guildId, roleManager);
        }

        return roleManager;
    }
}
