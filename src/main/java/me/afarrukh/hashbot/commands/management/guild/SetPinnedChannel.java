package me.afarrukh.hashbot.commands.management.guild;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import me.afarrukh.hashbot.utils.BotUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetPinnedChannel extends Command implements AdminCommand {

    public SetPinnedChannel() {
        super("setpinned");
        description =
                "Sets the pin channel for this server. This channel will only allow images to be sent. This should be typed"
                        + " in the channel that is to be set as the pinned channel.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        TextChannel channel = evt.getChannel().asTextChannel();
        GuildDataManager jgm = GuildDataMapper.getInstance().getDataManager(evt.getGuild());
        jgm.setPinnedChannel(channel.getId());
        channel.sendMessage("The new pinned channel for this server is " + channel.getName())
                .queue();
        BotUtils.deleteLastMsg(evt);
    }
}
