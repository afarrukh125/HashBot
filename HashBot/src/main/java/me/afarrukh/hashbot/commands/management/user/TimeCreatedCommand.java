package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Date;

public class TimeCreatedCommand extends Command {

    public TimeCreatedCommand() {
        super("timecreated");
        description = "Allows you to check the account creation date of any member on the server";
        addParameter("name", "**Optional**: The user name of the user you would like to search for. " +
                "Omitting this simply returns *your* creation date instead");
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        Member targetMember = null;
        if (params == null)
            if (!evt.getMessage().getMentionedMembers().isEmpty()) {
                targetMember = evt.getMessage().getMentionedMembers().get(0);
            } else
                targetMember = evt.getMember();
        else {
            if (evt.getGuild().getMembersByEffectiveName(params, true) != null)
                targetMember = evt.getGuild().getMembersByEffectiveName(params, true).get(0);
        }

        if (targetMember == null) {
            evt.getChannel().sendMessage("The user " + params + " does not exist.").queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder().setColor(Constants.EMB_COL);
        eb.setTitle(targetMember.getEffectiveName());
        long epochMilli = targetMember.getUser().getTimeCreated().toInstant().toEpochMilli();
        Date date = new Date(epochMilli);

        eb.appendDescription("Created on " + date.toString() + ".");
//         eb.appendDescription("The last time the user joined this server was " +
//                        new Date(targetMember.getJoinDate().toInstant().toEpochMilli()) + ".");

        evt.getChannel().sendMessage(eb.build()).queue();

    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
