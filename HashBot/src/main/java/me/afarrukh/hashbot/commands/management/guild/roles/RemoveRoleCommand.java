package me.afarrukh.hashbot.commands.management.guild.roles;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.RoleCommand;
import me.afarrukh.hashbot.gameroles.RoleRemover;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class RemoveRoleCommand extends Command implements RoleCommand {

    public RemoveRoleCommand() {
        super("removerole");
        addAlias("removeroles");
        description = "Lets you remove a role from your game roles";
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        evt.getMessage().delete().queue();
        RoleRemover rr = new RoleRemover(evt);
    }
}
