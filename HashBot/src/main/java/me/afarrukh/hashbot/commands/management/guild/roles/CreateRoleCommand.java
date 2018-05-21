package me.afarrukh.hashbot.commands.management.guild.roles;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.JSONGuildManager;
import me.afarrukh.hashbot.gameroles.RoleBuilder;
import me.afarrukh.hashbot.utils.BotUtils;
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
        if(evt.getGuild().getRoles().size() > 50) {
            evt.getChannel().sendMessage("Could not add role because there are too many roles.").queue();
            BotUtils.deleteLastMsg(evt);
            return;
        }
        RoleBuilder rb = new RoleBuilder(evt, params);
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
