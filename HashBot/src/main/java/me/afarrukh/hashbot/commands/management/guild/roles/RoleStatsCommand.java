package me.afarrukh.hashbot.commands.management.guild.roles;

import me.afarrukh.hashbot.commands.AdminCommand;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.RoleCommand;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.gameroles.GameRole;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.*;

public class RoleStatsCommand extends Command implements AdminCommand, RoleCommand {
    public RoleStatsCommand() {
        super("rolestats");
        addAlias("rs");
        description = "View how many members belong in each role.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        List<GameRole> roleList = new ArrayList<>(Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getGameRoles());

        if(roleList.isEmpty()) {
            evt.getTextChannel().sendMessage(new EmbedBuilder().setColor(Constants.EMB_COL).setTitle("No GameRoles on this server.")
            .setThumbnail(evt.getGuild().getIconUrl()).appendDescription("Use " + Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPrefix() + "createrole"
                    + " to create one.").build()).queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(evt.getGuild().getIconUrl());
        eb.setTitle("GameRole statistics for " + evt.getGuild().getName());

        Map<GameRole, Integer> roleMap = new HashMap<>();

        for(GameRole gr: roleList) {
            Role r = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getRoleFromGameRole(gr);
            if(r != null) {
                roleMap.put(gr, evt.getGuild().getMembersWithRoles(r).size());
            }
        }

        roleList.sort((o1, o2) -> {
            if(roleMap.get(o1) < roleMap.get(o2))
                return 1;
            else if(roleMap.get(o1).equals(roleMap.get(o2)))
                return 0;
            return -1;
        });

        int count = 1;
        for(GameRole r: roleList) {
            eb.appendDescription("`"+count + ".` **" + r.getName() + "**: " +roleMap.get(r)+ " members\n\n");
            count++;
        }

        evt.getTextChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
