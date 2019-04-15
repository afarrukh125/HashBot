package me.afarrukh.hashbot.commands.management.guild;

import me.afarrukh.hashbot.commands.AdminCommand;
import me.afarrukh.hashbot.commands.Command;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by Abdullah on 15/04/2019 16:12
 */
public class SetAutoPinChannelCommand extends Command implements AdminCommand {

    public SetAutoPinChannelCommand() {
        super("setautopin");
        addAlias("sp");
        addAlias("sap");

        description = "Sets whether a given channel should automatically pin messages to the pinned channel instead of the normal discord pinning method";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
