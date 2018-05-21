package me.afarrukh.hashbot.commands.management.guild.roles;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.JSONGuildManager;
import me.afarrukh.hashbot.gameroles.RoleAdder;
import me.afarrukh.hashbot.gameroles.RoleBuilder;
import me.afarrukh.hashbot.utils.BotUtils;
import me.afarrukh.hashbot.utils.UserUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

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
