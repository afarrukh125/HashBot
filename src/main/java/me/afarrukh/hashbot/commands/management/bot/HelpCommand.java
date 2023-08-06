package me.afarrukh.hashbot.commands.management.bot;

import com.google.inject.Guice;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.OwnerCommand;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.CommandManager;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.data.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends Command {

    private static final int MAX_DESCRIPTION_LENGTH = 1600;

    public HelpCommand(Database database) {
        super("help", database);

        description = "View the description for a specific command";
        addParameter("command name", "The name of the command to view an advanced for");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        var injector = Guice.createInjector(new CoreBotModule());
        var commandManager = injector.getInstance(CommandManager.class);
        if (params == null) {
            var embeds = getHelpMessageEmbeds(evt, commandManager);
            for (var eb : embeds) {
                evt.getChannel().sendMessageEmbeds(eb).queue();
            }
            return;
        }
        var command = commandManager.commandFromName(params);
        if (command == null) {
            evt.getChannel()
                    .sendMessage("There is no command with the name or alias " + params)
                    .queue();
            return;
        }

        evt.getChannel()
                .sendMessageEmbeds(
                        command.getCommandHelpMessage(evt.getChannel().asTextChannel()))
                .queue();
    }

    private List<MessageEmbed> getHelpMessageEmbeds(MessageReceivedEvent evt, CommandManager commandManager) {
        List<MessageEmbed> embedArrayList = new ArrayList<>();

        var initialEmbed = new EmbedBuilder().setColor(Constants.EMB_COL);
        initialEmbed.setTitle("Commands List (Page 1)");

        int pageCount = 2;

        var prefix = database.getPrefixForGuild(evt.getGuild().getId());

        StringBuilder sb = new StringBuilder();

        for (Command c : commandManager.getCommands()) {
            if (c instanceof OwnerCommand || c instanceof CommandListCommand) continue;

            int descLength = 0;
            if (c.getDescription() != null) descLength = c.getDescription().length();
            if (sb.toString().length() + descLength >= MAX_DESCRIPTION_LENGTH) {
                initialEmbed.appendDescription(sb.toString());
                initialEmbed.setThumbnail(evt.getJDA().getSelfUser().getAvatarUrl());
                sb = new StringBuilder();
                embedArrayList.add(initialEmbed.build());
                initialEmbed = new EmbedBuilder()
                        .setColor(Constants.EMB_COL)
                        .setTitle("Commands List (Page " + pageCount + ")");
                initialEmbed.setThumbnail(evt.getJDA().getSelfUser().getAvatarUrl());
                pageCount++;
            }

            appendCommandInformation(prefix, sb, c);
        }

        initialEmbed.setThumbnail(evt.getJDA().getSelfUser().getAvatarUrl());
        initialEmbed.appendDescription(sb.toString());

        initialEmbed.setFooter(
                "If you need help with a particular command, add the command name, e.g. " + prefix + "help play",
                evt.getJDA().getSelfUser().getAvatarUrl());
        embedArrayList.add(initialEmbed.build());
        return embedArrayList;
    }

    static void appendCommandInformation(String prefix, StringBuilder sb, Command c) {
        sb.append("**").append(prefix).append(c.getName()).append("**");

        if (!c.getAliases().isEmpty()) {
            List<String> aliases = new ArrayList<>(c.getAliases());
            sb.append(" (");
            for (int i = 0; i < aliases.size() - 1; i++)
                if (!aliases.get(i).equalsIgnoreCase(c.getName()))
                    sb.append(aliases.get(i)).append("/");

            if (!aliases.get(aliases.size() - 1).equalsIgnoreCase(c.getName()))
                sb.append(aliases.get(aliases.size() - 1));
            sb.append(")");
        }
        if (c.getDescription() != null) sb.append(" - ").append(c.getDescription());
        sb.append("\n\n");
    }
}
