package me.afarrukh.hashbot.extras;

import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class ExtrasManager {

    private final Map<Long, GuildExtrasManager> guildExtrasMap;

    public ExtrasManager() {
        guildExtrasMap = new HashMap<>();
    }

    public synchronized GuildExtrasManager getGuildExtrasManager(Guild guild) {
        long id = guild.getIdLong();
        GuildExtrasManager guildExtrasManager = guildExtrasMap.get(id);

        if(guildExtrasManager == null) {
            guildExtrasManager = new GuildExtrasManager(guild);
            guildExtrasMap.put(id, guildExtrasManager);
        }

        return guildExtrasManager;
    }
}
