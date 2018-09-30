package me.afarrukh.hashbot.commands.tagging;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class ViewCategoriesCommand extends Command {

    public ViewCategoriesCommand() {
        super("viewcategories");
        description = "Displays all command categories";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        List<String> stringList = new ArrayList<>();

        for(Command c: Bot.commandManager.getCommandList()) {
            if(c instanceof CategorisedCommand) {
                if(!stringList.contains(((CategorisedCommand) c).getType())) {
                    stringList.add(((CategorisedCommand) c).getType());
                }
            }
        }

        evt.getTextChannel().sendMessage(EmbedUtils.createCategoryEmbed(stringList, Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPrefix()))
                .queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
