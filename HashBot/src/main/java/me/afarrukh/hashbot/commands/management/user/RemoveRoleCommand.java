package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.gameroles.GameRole;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RemoveRoleCommand extends Command {

    public RemoveRoleCommand() {
        super("removerole");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        evt.getMessage().delete().queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
