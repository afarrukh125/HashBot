package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.AdminCommand;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.utils.BotUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SetNickCommand extends Command implements AdminCommand {

    public SetNickCommand() {
        super("setnick");
        description = "Sets the bot's nickname for this server/guild";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(!evt.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            evt.getChannel().sendMessage("Insufficient permission.").queue();
            BotUtils.deleteLastMsg(evt);
            evt.getMessage().delete().queue();
            return;
        }

        if(params == null) {
            onIncorrectParams(evt.getTextChannel());
            return;
        }

        Guild g = evt.getGuild();
        g.getController().setNickname(g.getMemberById(g.getJDA().getSelfUser().getId()), params).queue();
        evt.getChannel().sendMessage("Changed name to " +params).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: setname <newName>").queue();
    }
}
