package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.CommandManager;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", new String[]{"cmds", "cmd"});
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        evt.getTextChannel().sendMessage(EmbedUtils.getHelpMsg(evt)).queue();

    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
