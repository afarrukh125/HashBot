package me.afarrukh.hashbot.commands.management.guild.roles;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.JSONGuildManager;
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
        List<Role> normalRoleList = new ArrayList<>();

        //Not considering any roles with administrator permission
        for(Role r: evt.getGuild().getRoles()) {
            if(r.getPermissions().contains(Permission.ADMINISTRATOR))
                continue;
            normalRoleList.add(r);
        }

        String desiredRole = params;

        for(Role r: normalRoleList) {
            if(r.getName().equalsIgnoreCase(params) && (r.getPosition() < UserUtils.getHighestRolePosition(evt.getMember()))) {
                if(evt.getMember().getRoles().contains(r)) {
                    evt.getTextChannel().sendMessage("You already have this role.").queue();
                    return;
                }
                evt.getGuild().getController().addSingleRoleToMember(evt.getMember(), r).queue();
                evt.getChannel().sendMessage("You have joined role: " +r.getName()).queue();
                return;
            }
        }
        if(evt.getGuild().getRoles().size() > 50) {
            evt.getChannel().sendMessage("Could not add role because there are too many roles.").queue();
            return;
        }
        for(Role r: evt.getGuild().getRoles())
            if(r.getName().equalsIgnoreCase(params)) {
                evt.getTextChannel().sendMessage("The role already exists but you cannot join it.").queue();
                return;
            }
        Role newRole = evt.getGuild().getController().createRole().complete();

        String cap = params.substring(0, 1).toUpperCase() + params.substring(1);

        newRole.getManager().setName(cap).setMentionable(true).setHoisted(false)
                .queue();

        evt.getGuild().getController().addSingleRoleToMember(evt.getMember(), newRole).queue();

        evt.getChannel().sendMessage("You have created and joined role: " +cap).queue();
        JSONGuildManager gfm = new JSONGuildManager(evt.getGuild());

        gfm.addRole(cap, 92, 70, 161);
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
