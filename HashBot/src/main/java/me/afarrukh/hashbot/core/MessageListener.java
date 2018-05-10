package me.afarrukh.hashbot.core;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import me.afarrukh.hashbot.config.Config;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
        if(evt.isFromType(ChannelType.PRIVATE))
            return;
        if(evt.getAuthor().isBot())
            return;
        if(evt.getMessage().getContentRaw().startsWith(Config.invokerChar))
            Bot.commandManager.processEvent(evt);
    }
}
