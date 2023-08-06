package me.afarrukh.hashbot.commands.management.guild;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.data.Database;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class SetUnpinnedCommand extends Command implements AdminCommand {

    public SetUnpinnedCommand(Database database) {
        super("setunpinned", database);
        description = "Unsets the pinned channel for this server. This can be typed in any channel";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        database.unsetPinnedChannelForGuild(evt.getGuild().getId());
        Message message = evt.getChannel()
                .sendMessage("There is no longer a pinned channel for this server.")
                .complete();
        message.delete().queueAfter(2, TimeUnit.SECONDS);
    }
}
