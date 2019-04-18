package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.CategorisedCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", new String[]{"cmds", "cmd"});
        description = "Displays all commands provide a parameter e.g. music to see commands only of that category";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        List<Command> commandList = evt.getMember().hasPermission(Permission.ADMINISTRATOR) ? Bot.commandManager.getCommandList() : Bot.commandManager.getNonAdminCommands();

        if(params == null) {
            for (MessageEmbed embed : EmbedUtils.getHelpMsg(evt, commandList))
                evt.getTextChannel().sendMessage(embed).queue();

        }
        else {
            List<Command> categoryList = new ArrayList<>();
            for(Command c: commandList) {
                if(c instanceof CategorisedCommand) {
                    if(((CategorisedCommand) c).getType().equalsIgnoreCase(params)) {
                        categoryList.add(c);
                    }
                }
            }
            if(categoryList.isEmpty())
                categoryList = Bot.commandManager.getCommandList();
            for(MessageEmbed embed: EmbedUtils.getHelpMsg(evt, categoryList)) {
                evt.getTextChannel().sendMessage(embed).queue();
            }
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
