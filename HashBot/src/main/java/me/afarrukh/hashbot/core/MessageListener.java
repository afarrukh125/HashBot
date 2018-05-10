package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.entities.Invoker;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import me.afarrukh.hashbot.config.Constants;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
        if(evt.isFromType(ChannelType.PRIVATE))
            return;
        if(evt.getAuthor().isBot())
            return;
        if(!evt.getMessage().getAttachments().isEmpty())
            return;
        if(evt.getMessage().getContentRaw().startsWith(Constants.invokerChar)) {
            Bot.commandManager.processEvent(evt);
            return;
        }
        Invoker invoker = new Invoker(evt.getMember());
        if(invoker.hasTimePassed()) {
            invoker.addRandomCredit();
            invoker.updateExperience(evt.getMessage().getContentRaw());
        }
    }
}
