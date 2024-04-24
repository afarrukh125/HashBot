package me.afarrukh.hashbot;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import me.afarrukh.hashbot.config.Config;
import me.afarrukh.hashbot.core.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class JDAModule extends AbstractModule {

    @Provides
    @Singleton
    public JDA jda(Config config, MessageListener messageListener) throws InterruptedException {
        return JDABuilder.create(
                        config.getBotToken(),
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.MESSAGE_CONTENT)
                .disableCache(
                        CacheFlag.ACTIVITY,
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.EMOJI,
                        CacheFlag.SCHEDULED_EVENTS,
                        CacheFlag.STICKER)
                .addEventListeners(messageListener)
                .build()
                .awaitReady();
    }
}
