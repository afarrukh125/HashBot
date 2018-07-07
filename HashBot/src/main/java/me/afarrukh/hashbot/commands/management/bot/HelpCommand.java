package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.CommandManager;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", new String[]{"cmds", "cmd"});
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        for(MessageEmbed embed: EmbedUtils.getHelpMsg(evt))
        evt.getTextChannel().sendMessage(embed).queue();

    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
