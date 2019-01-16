package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.RoleCommand;
import me.afarrukh.hashbot.gameroles.RoleRemover;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RemoveRoleCommand extends Command implements RoleCommand {

    public RemoveRoleCommand() {
        super("removerole");
        description = "Lets you remove a role from your game roles";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        evt.getMessage().delete().queue();
        RoleRemover rr = new RoleRemover(evt);
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
