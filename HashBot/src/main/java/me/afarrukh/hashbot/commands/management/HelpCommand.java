package me.afarrukh.hashbot.commands.management;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

/**
 * @author Abdullah
 * Created on 16/09/2019 at 11:47
 */
public class HelpCommand extends Command {

    public HelpCommand() {
        super("help");

        description = "View the description for a specific command";
        addParameter("command name", "The name of the command to view an advanced for");
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        if(params == null) {
            List<MessageEmbed> embeds = EmbedUtils.getHelpMsg(evt, Bot.commandManager.getCommandList());
            for(MessageEmbed eb: embeds) {
                evt.getChannel().sendMessage(eb).queue();
            }
            evt.getChannel().sendMessage("If you wish to view the help for an individual command, you can " +
                    "provide it as a parameter to this command").queue();
            return;
        }

        Command command = Bot.commandManager.commandFromName(params);
        if(command == null) {
            evt.getChannel().sendMessage("There is no command with the name or alias " + params).queue();
            return;
        }

        evt.getChannel().sendMessage(command.getCommandHelpMessage(evt.getChannel())).queue();
    }
}
