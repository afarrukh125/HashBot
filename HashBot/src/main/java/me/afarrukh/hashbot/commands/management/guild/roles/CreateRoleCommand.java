package me.afarrukh.hashbot.commands.management.guild.roles;

import me.afarrukh.hashbot.commands.AdminCommand;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.RoleCommand;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.gameroles.RoleBuilder;
import me.afarrukh.hashbot.utils.BotUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CreateRoleCommand extends Command implements RoleCommand, AdminCommand {

    public CreateRoleCommand() {
        super("createrole");
        description = "Allows you to add to this server's current list of (game) roles";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        evt.getMessage().delete().queue();
        if(!evt.getMember().hasPermission(Permission.ADMINISTRATOR))
            return;
        if(evt.getGuild().getRoles().size() > 50) {
            evt.getChannel().sendMessage("Could not add role because there are too many roles.").queue();
            BotUtils.deleteLastMsg(evt);
            return;
        }

        if(new Invoker(evt.getMember()).getCredit() < Constants.ROLE_CREATE_AMOUNT) {
            evt.getChannel().sendMessage("Could not add role because you do not have enough credit (Requires "
                    +Constants.ROLE_CREATE_AMOUNT+")").queue();
            BotUtils.deleteLastMsg(evt);
            return;
        }

        RoleBuilder rb = new RoleBuilder(evt, params);
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
