package me.afarrukh.hashbot.commands.management.guild;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetPrefixCommand extends Command implements AdminCommand {

    public SetPrefixCommand() {
        super("setprefix");
        description = "Sets the bot prefix";
        addParameter("prefix", "The new prefix for the bot");
        addExampleUsage("setprefix ,");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        if (params != null && params.length() < 2) {
            Bot.prefixManager.getGuildRoleManager(evt.getGuild())
                    .setPrefix(params);
            evt.getChannel().sendMessage("Bot prefix is now " + params).queue();
        } else {
            onIncorrectParams(evt.getChannel().asTextChannel());
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: setprefix <prefix> \nThis must be at most 1 character and non empty").queue();
    }
}
