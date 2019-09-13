package me.afarrukh.hashbot.commands.management.guild.roles;

import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.RoleCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import me.afarrukh.hashbot.utils.BotUtils;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class SetRoleCommand extends Command implements AdminCommand, RoleCommand {

    public SetRoleCommand() {
        super("setrole");
        addAlias("sr");
        description = "Allows you to add an existing role to the list of the servers gameroles. This role must not have administrator permissions.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(params == null) {
            onIncorrectParams(evt.getTextChannel());
            return;
        }

        List<Role> roleList = evt.getGuild().getRolesByName(params, true);

        if(roleList.size() == 0) {
            evt.getTextChannel().sendMessage("There is no such role on this server.").queue();
            return;
        }

        Role targetRole = roleList.get(0);
        if(BotUtils.isValidGameRole(targetRole)) {
            evt.getTextChannel().sendMessage("The role must not have administrator permissions.").queue();
            return;
        }

        if(Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getGameRoles()
                .contains(Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getGameRoleFromRole(targetRole))) {
            evt.getTextChannel().sendMessage("The role is already in the list of the server's game roles.").queue();
            return;
        }

        if(targetRole.getPosition() >= evt.getGuild().getMemberById(evt.getJDA().getSelfUser().getId()).getRoles().get(0).getPosition()) {
            evt.getTextChannel().sendMessage("Cannot add this role to the list of game roles for this server as it is higher up than the bot in the hierarchy.")
                    .queue();
            return;
        }
        GuildDataManager jgm = GuildDataMapper.getInstance().getDataManager(evt.getGuild());
        jgm.addRole(targetRole.getName(), evt.getAuthor().getId());

        evt.getTextChannel().sendMessage("The role " +targetRole.getName()+ " has been added to the list of game roles for this server.").queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("You must provide the name of the role you would like to add to the servers list of game roles.").queue();
    }
}

