package me.afarrukh.hashbot.commands.management.guild;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class SetUnpinnedCommand extends Command implements AdminCommand {

    public SetUnpinnedCommand() {
        super("setunpinned");
        description = "Unsets the pinned channel for this server. This can be typed in any channel";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        GuildDataManager jgm = GuildDataMapper.getInstance().getDataManager(evt.getGuild());
        jgm.unsetPinnedChannel();
        Message message = evt.getTextChannel().sendMessage("There is no longer a pinned channel for this server.").complete();
        message.delete().queueAfter(2, TimeUnit.SECONDS);
    }
}
