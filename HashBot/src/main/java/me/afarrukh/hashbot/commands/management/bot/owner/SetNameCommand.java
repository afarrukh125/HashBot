package me.afarrukh.hashbot.commands.management.bot.owner;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.OwnerCommand;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SetNameCommand extends Command implements OwnerCommand {

    public SetNameCommand() {
        super("setname");
        description = "Sets the global discord username for the JDA instance";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        evt.getJDA().getSelfUser().getManager().setName(params).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
