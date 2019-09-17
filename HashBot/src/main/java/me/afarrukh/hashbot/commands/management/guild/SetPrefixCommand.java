package me.afarrukh.hashbot.commands.management.guild;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class SetPrefixCommand extends Command implements AdminCommand {

    public SetPrefixCommand() {
        super("setprefix");
        description = "Sets the bot prefix";
        addParameter("prefix", "The new prefix for the bot");
        addExampleUsage("setprefix ,");
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {

        if (params != null && params.length() < 2) {
            Bot.gameRoleManager.getGuildRoleManager(evt.getGuild())
                    .setPrefix(params);
            evt.getChannel().sendMessage("Bot prefix is now " + params).queue();
        } else {
            onIncorrectParams(evt.getChannel());
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: setprefix <prefix> \nThis must be at most 1 character and non empty").queue();
    }
}
