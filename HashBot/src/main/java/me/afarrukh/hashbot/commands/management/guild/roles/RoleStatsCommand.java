package me.afarrukh.hashbot.commands.management.guild.roles;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.RoleCommand;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleStatsCommand extends Command implements RoleCommand {
    public RoleStatsCommand() {
        super("rolestats");
        addAlias("rs");
        addAlias("rolecount");
        addAlias("rc");
        description = "View how many members belong in each role.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        List<Role> roleList = new ArrayList<>(Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getGameRolesAsRoles());

        if (roleList.isEmpty()) {
            evt.getTextChannel().sendMessage(new EmbedBuilder().setColor(Constants.EMB_COL).setTitle("No GameRoles on this server.")
                    .setThumbnail(evt.getGuild().getIconUrl()).appendDescription("Use " + Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPrefix() + "createrole"
                            + " to create one.").build()).queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(evt.getGuild().getIconUrl());
        eb.setTitle("GameRole statistics for " + evt.getGuild().getName() + " [Total " + filterBots(evt).size() + " members]");

        Map<Role, Integer> roleMap = new HashMap<>();

        for (Role r : roleList) {
            roleMap.put(r, evt.getGuild().getMembersWithRoles(r).size());
        }

        roleList.sort((o1, o2) -> {
            if (roleMap.get(o1) < roleMap.get(o2))
                return 1;
            else if (roleMap.get(o1).equals(roleMap.get(o2)))
                return 0;
            return -1;
        });

        int count = 1;
        for (Role r : roleList) {
            eb.appendDescription("`" + count + ".` **" + r.getName() + "**: " + roleMap.get(r) + " members\n\n");
            count++;
        }

        eb.setFooter("You can check which members have this role using " + new ListMembersCommand().getName() + " command.", evt.getMember().getUser().getAvatarUrl());
        evt.getTextChannel().sendMessage(eb.build()).queue();
    }

    private List<Member> filterBots(MessageReceivedEvent evt) {
        List<Member> memberList = new ArrayList<>(evt.getGuild().getMembers());

        memberList.removeIf(m -> m.getUser().isBot());
        return memberList;
    }
}
