package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.utils.BotUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetNickCommand extends Command implements AdminCommand {

    public SetNickCommand() {
        super("setnick");
        description = "Sets the bot's nickname for this server/guild";
        addParameter("name", "The name to set for the local server/guild");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!evt.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            evt.getChannel().sendMessage("Insufficient permission.").queue();
            BotUtils.deleteLastMsg(evt);
            evt.getMessage().delete().queue();
            return;
        }

        if (params == null) {
            onIncorrectParams(evt.getChannel().asTextChannel());
            return;
        }

        Guild g = evt.getGuild();
        g.modifyNickname(g.getMemberById(g.getJDA().getSelfUser().getId()), params).queue();
        evt.getChannel().sendMessage("Changed name to " + params).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: setname <newName>").queue();
    }
}
