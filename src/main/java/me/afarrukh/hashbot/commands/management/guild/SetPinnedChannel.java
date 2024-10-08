package me.afarrukh.hashbot.commands.management.guild;

import java.util.concurrent.TimeUnit;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.data.Database;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetPinnedChannel extends Command implements AdminCommand {

    private final Database database;

    public SetPinnedChannel(Database database) {
        super("setpinned");
        this.database = database;
        description =
                "Sets the pin channel for this server. This channel will only allow images to be sent. This should be typed"
                        + " in the channel that is to be set as the pinned channel.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        TextChannel channel = evt.getChannel().asTextChannel();

        database.setPinnedChannelForGuild(evt.getGuild().getId(), channel.getId());
        channel.sendMessage("The new pinned channel for this server is " + channel.getName())
                .queueAfter(
                        1500, TimeUnit.MILLISECONDS, message -> message.delete().queue());
    }
}
