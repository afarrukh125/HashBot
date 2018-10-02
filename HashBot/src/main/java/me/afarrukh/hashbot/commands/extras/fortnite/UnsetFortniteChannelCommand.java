package me.afarrukh.hashbot.commands.extras.fortnite;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.ExtrasCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.extras.Extra;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class UnsetFortniteChannelCommand extends Command implements ExtrasCommand {

    public UnsetFortniteChannelCommand() {
        super("unsetftnchannel");
        description = "Removes the fortnite channel";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(!evt.getMember().hasPermission(Permission.ADMINISTRATOR))
            return;
        Bot.extrasManager.getGuildExtrasManager(evt.getGuild()).getFortniteExtra().unsetFortniteChannel();
        evt.getMessage().delete().queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
