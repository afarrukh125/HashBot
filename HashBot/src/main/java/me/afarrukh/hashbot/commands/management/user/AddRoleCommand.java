package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.gameroles.RoleAdder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class AddRoleCommand extends Command {

    public AddRoleCommand() {
        super("addrole");
        description = "Gives you a selection of all the roles you can join on this server. These are usually game related roles";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        evt.getMessage().delete().queue();
        RoleAdder ra = new RoleAdder(evt);
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}