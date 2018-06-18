package me.afarrukh.hashbot.commands.management.guild;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.JSONGuildManager;
import me.afarrukh.hashbot.utils.BotUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SetPinnedChannel extends Command {

    public SetPinnedChannel() {
        super("setpinned");
        description = "Sets the pin channel for this server. This channel will only allow images to be sent";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(!evt.getMember().hasPermission(Permission.ADMINISTRATOR))
            return;

        TextChannel channel = evt.getTextChannel();
        JSONGuildManager jgm = new JSONGuildManager(evt.getGuild());
        jgm.updateField("pinnedchannel", channel.getId());
        channel.sendMessage("The new pinned channel for this server is " +channel.getName()).queue();
        BotUtils.deleteLastMsg(evt);
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
