package me.afarrukh.hashbot.commands.management.guild;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.JSONGuildManager;
import me.afarrukh.hashbot.utils.BotUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SetUnpinned extends Command {

    public SetUnpinned() {
        super("setunpinned");
        description = "Unsets the pinned message channel for this server";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(!evt.getMember().hasPermission(Permission.ADMINISTRATOR))
            return;

        JSONGuildManager jgm = new JSONGuildManager(evt.getGuild());
        jgm.updateField("pinnedchannel", "");
        evt.getTextChannel().sendMessage("There is no longer a pinned channel for this server.").queue();
        BotUtils.deleteLastMsg(evt);
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
