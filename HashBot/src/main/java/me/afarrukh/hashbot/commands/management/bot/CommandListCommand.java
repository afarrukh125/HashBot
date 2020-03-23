package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.CategorisedCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandListCommand extends Command {

    public CommandListCommand() {
        super("commands");
        addAlias("cmds");
        description = "Displays all commands provide a parameter e.g. music to see commands only of that category";
        addParameter("category", "**Optional**: The category to filter commands for.");
        addExampleUsage("help roles");
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        List<Command> commandList = evt.getMember().hasPermission(Permission.ADMINISTRATOR) ? Bot.commandManager.getCommandList() : Bot.commandManager.getNonAdminCommands();

        if (params == null) {
            for (MessageEmbed embed : EmbedUtils.getHelpMsg(evt, commandList))
                evt.getChannel().sendMessage(embed).queue();

        } else {
            List<Command> categoryList = new ArrayList<>();
            for (Command c : commandList) {
                if (c instanceof CategorisedCommand) {
                    if (((CategorisedCommand) c).getType().equalsIgnoreCase(params)) {
                        categoryList.add(c);
                    }
                }
            }
            if (categoryList.isEmpty())
                categoryList = Bot.commandManager.getCommandList();
            for (MessageEmbed embed : EmbedUtils.getHelpMsg(evt, categoryList)) {
                evt.getChannel().sendMessage(embed).queue();
            }
        }
    }
}
