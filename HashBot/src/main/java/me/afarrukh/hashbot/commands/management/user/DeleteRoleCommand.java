package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.gameroles.RoleDeleter;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class DeleteRoleCommand extends Command {

    public DeleteRoleCommand() {
        super("deleterole");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        evt.getMessage().delete().queue();
        RoleDeleter rd = new RoleDeleter(evt);
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
