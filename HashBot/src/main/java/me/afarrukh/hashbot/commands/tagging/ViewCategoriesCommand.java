package me.afarrukh.hashbot.commands.tagging;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViewCategoriesCommand extends Command {

    public ViewCategoriesCommand() {
        super("viewcategories");
        addAlias("vc");
        description = "Displays all command categories";
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        Set<String> stringList = new HashSet<>();

        for (Command c : Bot.commandManager.getCommandList()) {
            if (c instanceof CategorisedCommand) {
                stringList.add(((CategorisedCommand) c).getType());
            }
        }

        evt.getChannel().sendMessage(EmbedUtils.createCategoryEmbed(stringList, Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPrefix()))
                .queue();
    }
}
