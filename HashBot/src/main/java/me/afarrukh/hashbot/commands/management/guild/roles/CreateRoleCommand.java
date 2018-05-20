package me.afarrukh.hashbot.commands.management.guild.roles;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.JSONGuildManager;
import me.afarrukh.hashbot.gameroles.RoleBuilder;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CreateRoleCommand extends Command {

    public CreateRoleCommand() {
        super("createrole");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        evt.getMessage().delete().queue();
//        Role newRole = evt.getGuild().getController().createRole().complete();
//
//        String cap = params.substring(0, 1).toUpperCase() + params.substring(1);

        RoleBuilder rb = new RoleBuilder(evt);

//        newRole.getManager().setName(cap).setMentionable(true).setHoisted(false)
//                .queue();
//
//        evt.getGuild().getController().addSingleRoleToMember(evt.getMember(), newRole).queue();
//
//        evt.getChannel().sendMessage("You have created and joined role: " +cap).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
