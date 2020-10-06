package me.afarrukh.hashbot.commands.management.guild.roles;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.commands.tagging.RoleCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.gameroles.RoleBuilder;
import me.afarrukh.hashbot.utils.BotUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CreateRoleCommand extends Command implements RoleCommand, AdminCommand {

    public CreateRoleCommand() {
        super("createrole");
        description = "Allows you to add to this server's current list of (game) roles";
        addParameter("role name", "**Optional**: The name of the new role to be created");
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {

        evt.getMessage().delete().queue();
        if (!evt.getMember().hasPermission(Permission.ADMINISTRATOR))
            return;
        if (Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getGameRoles().size() > 50) {
            evt.getChannel().sendMessage("Could not add role because there are too many roles.").queue();
            BotUtils.deleteLastMsg(evt);
            return;
        }

        new RoleBuilder(evt, params);
    }
}
