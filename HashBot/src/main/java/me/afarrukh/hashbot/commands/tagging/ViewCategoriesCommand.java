package me.afarrukh.hashbot.commands.tagging;

import me.afarrukh.hashbot.commands.Command;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ViewCategoriesCommand extends Command {

    public ViewCategoriesCommand(String name, String[] aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
