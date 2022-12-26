package me.afarrukh.hashbot.data;

import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Abdullah on 15/04/2019 17:08
 * <p>
 * This class aims to help get rid of the file IO involved when dealing with the guild data managers
 */
public class GuildDataMapper {

    private static GuildDataMapper instance;
    private final Map<String, GuildDataManager> dataManagerMap;

    private GuildDataMapper() {
        this.dataManagerMap = new HashMap<>();
    }

    public static GuildDataMapper getInstance() {
        if (instance == null) {
            instance = new GuildDataMapper();
        }
        return instance;
    }

    public synchronized GuildDataManager getDataManager(Guild guild) {
        String guildId = guild.getId();

        if (dataManagerMap.get(guildId) == null) {
            dataManagerMap.put(guildId, new GuildDataManager(guild));
        }

        return dataManagerMap.get(guildId);
    }
}
