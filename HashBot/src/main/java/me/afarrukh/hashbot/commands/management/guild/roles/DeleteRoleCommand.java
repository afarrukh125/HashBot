package me.afarrukh.hashbot.commands.management.guild.roles;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.commands.tagging.RoleCommand;
import me.afarrukh.hashbot.gameroles.RoleDeleter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DeleteRoleCommand extends Command implements RoleCommand, AdminCommand {

    public DeleteRoleCommand() {
        super("deleterole");
        description = "Lets you delete a role from the server. Unless you are admin you can only delete ones you have created through this bot.";

    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        evt.getMessage().delete().queue();
        if (!evt.getMember().hasPermission(Permission.ADMINISTRATOR))
            return;

        RoleDeleter rd = new RoleDeleter(evt);
    }
}
