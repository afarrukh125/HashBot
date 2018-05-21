package me.afarrukh.hashbot.commands.management.guild.roles;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.gameroles.RoleAdder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class AddRoleCommand extends Command {

    public AddRoleCommand() {
        super("addrole");
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
