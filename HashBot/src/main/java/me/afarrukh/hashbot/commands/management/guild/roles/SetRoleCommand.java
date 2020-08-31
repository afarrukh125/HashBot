package me.afarrukh.hashbot.commands.management.guild.roles;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.commands.tagging.RoleCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import me.afarrukh.hashbot.gameroles.GameRole;
import me.afarrukh.hashbot.utils.BotUtils;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class SetRoleCommand extends Command implements AdminCommand, RoleCommand {

    public SetRoleCommand() {
        super("setrole");
        addAlias("sr");
        description = "Allows you to add an existing role to the list of the servers gameroles. This role must not have administrator permissions.";
        addParameter("role name", "The role that you would like to add to the list of server gameroles");
        addExampleUsage("setrole role");
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        if (params == null) {
            onIncorrectParams(evt.getChannel());
            return;
        }

        List<Role> roleList = evt.getGuild().getRolesByName(params, true);

        if (roleList.size() == 0) {
            evt.getChannel().sendMessage("There is no such role on this server.").queue();
            return;
        }

        Role targetRole = roleList.get(0);
        if (BotUtils.isValidGameRole(targetRole)) {
            evt.getChannel().sendMessage("The role must not have administrator permissions.").queue();
            return;
        }

        if (Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getGameRoles()
                .contains(Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getGameRoleFromRole(targetRole))) {
            evt.getChannel().sendMessage("The role is already in the list of the server's game roles.").queue();
            return;
        }

        if (targetRole.getPosition() >= evt.getGuild().getMemberById(evt.getJDA().getSelfUser().getId()).getRoles().get(0).getPosition()) {
            evt.getChannel().sendMessage("Cannot add this role to the list of game roles for this server as it is higher up than the bot in the hierarchy.")
                    .queue();
            return;
        }

        GuildDataManager jgm = GuildDataMapper.getInstance().getDataManager(evt.getGuild());

        // Add to database for persistence
        jgm.addRole(targetRole.getName(), evt.getAuthor().getId());

        // Add it to the live game role manager object
        Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).addGameRole(new GameRole(targetRole.getName(), Objects.requireNonNull(evt.getMember()).getId()), targetRole);

        evt.getChannel().sendMessage("The role " + targetRole.getName() + " has been added to the list of game roles for this server.").queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("You must provide the name of the role you would like to add to the servers list of game roles.").queue();
    }
}

