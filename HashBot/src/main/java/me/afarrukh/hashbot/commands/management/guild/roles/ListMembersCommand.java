package me.afarrukh.hashbot.commands.management.guild.roles;

import me.afarrukh.hashbot.commands.AdminCommand;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Abdullah on 15/04/2019 16:19
 */
public class ListMembersCommand extends Command implements AdminCommand {

    public ListMembersCommand() {
        super("listmembers");
        addAlias("lm");

        description = "Shows the names of members with the provided role";

    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(params == null) {
            onIncorrectParams(evt.getTextChannel());
            return;
        }

        Role r = evt.getGuild().getRolesByName(params, true).get(0); // Grab role by the name

        if(r == null) { // If the role does not exist...
            evt.getTextChannel().sendMessage("The role you have provided does not exist.").queue();
            return;
        }

        List<Member> memberList = evt.getGuild().getMembersWithRoles(r);

        memberList.sort(new Comparator<Member>() {
            @Override
            public int compare(Member o1, Member o2) {
                if(o1.getRoles().get(0).getPosition() < o2.getRoles().get(0).getPosition())
                    return 1;
                if (o1.getRoles().get(0).getPosition() == o2.getRoles().get(0).getPosition()) {
                    return Integer.compare(o1.getEffectiveName().toLowerCase().compareTo(o2.getEffectiveName().toLowerCase()), 0);
                }
                return -1;

            }
        });

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(evt.getGuild().getIconUrl());
        eb.setTitle(memberList.size() + " members with role " + r.getName());

        if(memberList.isEmpty()) { // If we find there are no members with the role
            eb.setDescription("There are no members with this role.");
            evt.getTextChannel().sendMessage(eb.build()).queue();
            return;
        }

        for(int i = 0; i<memberList.size(); i++)
            eb.appendDescription("`" + (i+1) + ".`  " + memberList.get(i).getEffectiveName() + "\n");

        evt.getTextChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Please provide a role name to list members for.").queue();
    }
}
