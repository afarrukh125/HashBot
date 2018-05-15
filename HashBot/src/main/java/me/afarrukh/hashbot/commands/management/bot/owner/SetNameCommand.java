package me.afarrukh.hashbot.commands.management.bot.owner;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SetNameCommand extends OwnerCommand {

    public SetNameCommand() {
        super("setname");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        evt.getJDA().getSelfUser().getManager().setName(params).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
