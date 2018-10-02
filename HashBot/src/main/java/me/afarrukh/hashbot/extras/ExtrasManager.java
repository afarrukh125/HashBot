package me.afarrukh.hashbot.extras;

import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class ExtrasManager extends ListenerAdapter {

    private final Map<Long, GuildExtrasManager> guildExtrasMap;

    public ExtrasManager() {
        guildExtrasMap = new HashMap<>();

        for(Guild guild: Bot.botUser.getGuilds()) {
            getGuildExtrasManager(guild);
        }
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

    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
        if(evt.getMember().getUser().getId().equalsIgnoreCase(Bot.botUser.getSelfUser().getId()))
            return;
        getGuildExtrasManager(evt.getGuild()).processEvent(evt);
    }

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent evt) {
        getGuildExtrasManager(evt.getGuild()).processEvent(evt);
    }
}
