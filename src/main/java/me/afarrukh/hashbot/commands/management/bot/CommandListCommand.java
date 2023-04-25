package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.OwnerCommand;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

import static me.afarrukh.hashbot.commands.management.bot.HelpCommand.appendCommandInformation;

public class CommandListCommand extends Command {

    public CommandListCommand() {
        super("commands");
        addAlias("cmds");
        description = "Displays all commands provide a parameter e.g. track to see commands only of that category";
        addExampleUsage("help roles");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        List<Command> commandList = evt.getMember().hasPermission(Permission.ADMINISTRATOR) ? Bot.commandManager.getCommands() : Bot.commandManager.getNonAdminCommands();

        List<MessageEmbed> embedArrayList = new ArrayList<>();

        EmbedBuilder eb = new EmbedBuilder().setColor(Constants.EMB_COL);
        eb.setTitle("Commands List (Page 1)");

        int pageCount = 2;

        String prefix = Bot.prefixManager.getGuildRoleManager(evt.getGuild()).getPrefix();

        StringBuilder sb = new StringBuilder();

        for (Command c : commandList) {
            if (c instanceof OwnerCommand || c instanceof CommandListCommand)
                continue;

            int descLength = 0;
            if (c.getDescription() != null)
                descLength = c.getDescription().length();
            if (sb.toString().length() + descLength >= 1600) {
                eb.appendDescription(sb.toString());
                eb.setThumbnail(evt.getJDA().getSelfUser().getAvatarUrl());
                sb = new StringBuilder();
                embedArrayList.add(eb.build());
                eb = new EmbedBuilder().setColor(Constants.EMB_COL).setTitle("Commands List (Page " + pageCount + ")");
                eb.setThumbnail(evt.getJDA().getSelfUser().getAvatarUrl());
                pageCount++;
            }

            appendCommandInformation(prefix, sb, c);
        }

        eb.setThumbnail(evt.getJDA().getSelfUser().getAvatarUrl());
        eb.appendDescription(sb.toString());

        eb.setFooter("If you need help with a particular command, add the command name, e.g. " +
                prefix + "help play", evt.getJDA().getSelfUser().getAvatarUrl());
        embedArrayList.add(eb.build());
        if (params == null)
            for (MessageEmbed embed : embedArrayList)
                evt.getChannel().sendMessageEmbeds(embed).queue();
    }
}
