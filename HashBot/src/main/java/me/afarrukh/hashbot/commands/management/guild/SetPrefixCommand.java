package me.afarrukh.hashbot.commands.management.guild;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SetPrefixCommand extends Command {

    public SetPrefixCommand() {
        super("setprefix");
        description = "Sets the bot prefix";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(!evt.getMember().hasPermission(Permission.ADMINISTRATOR))
            return;

        if(params!=null && params.length() < 2) {
            Bot.gameRoleManager.getGuildRoleManager(evt.getGuild())
                    .setPrefix(params);
            evt.getTextChannel().sendMessage("Bot prefix is now " + params).queue();
        } else {
            onIncorrectParams(evt.getTextChannel());
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: setprefix <prefix> \nThis must be at most 1 character and non empty").queue();
    }
}
