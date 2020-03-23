package me.afarrukh.hashbot.commands.management.guild.roles;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.RoleCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.gameroles.GameRole;
import me.afarrukh.hashbot.gameroles.RoleAdder;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class AddRoleCommand extends Command implements RoleCommand {

    public AddRoleCommand() {
        super("addrole");
        addAlias("addroles");
        description = "Gives you a list of all the game roles you can join on this server. You can also specify the role by providing the role name as a parameter.";
        addParameter("role name", "**Optional**: The name of the role you would like to join");
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {

        evt.getMessage().delete().queue();

        if (params != null) {
            List<Role> roleList = evt.getGuild().getRolesByName(params, true);

            GameRole gr = roleList.size() == 0 ? null : Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getGameRoleFromRole(roleList.get(0));

            if (gr != null) {
                Role r = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getRoleFromGameRole(gr);
                if (evt.getMember().getRoles().contains(r)) {
                    evt.getChannel().sendMessage(EmbedUtils.alreadyHasRoleEmbed(r))
                            .queue();
                } else {
                    evt.getGuild().addRoleToMember(evt.getMember(),
                            Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getRoleFromGameRole(gr)).queue();
                    evt.getChannel().sendMessage(EmbedUtils.addRoleCompleteEmbed(r)).queue();
                }
            } else {
                evt.getChannel().sendMessage(EmbedUtils.getNullRoleEmbed(evt.getGuild())).queue();
            }
        } else {
            new RoleAdder(evt);
        }
    }
}
