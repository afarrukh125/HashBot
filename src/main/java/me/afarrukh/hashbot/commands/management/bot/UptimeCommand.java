package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.SystemCommand;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UptimeCommand extends Command implements SystemCommand {

    public UptimeCommand() {
        super("uptime");
        addAlias("up");
        description = "Displays the bot uptime in HH:MM:SS";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        long now = System.currentTimeMillis();
        long upTimeUnix = now - Constants.timeStarted;
        long seconds = (int) (upTimeUnix / 1000) % 60;
        int minutes = (int) (upTimeUnix / 1000 / 60) % 60;
        int hours = (int) (upTimeUnix / 1000 / 60 / 60) % 24;
        int days = (int) (upTimeUnix / 1000 / 60 / 60) / 24;

        evt.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setColor(Constants.EMB_COL)
                .appendDescription("Bot uptime: " + days + " days, "
                        + hours + " hours, " + minutes + " minutes and " + seconds + " seconds.\n\n")
                .appendDescription("There have been " + Bot.commandManager.getCommandCount() + " commands executed this session.")
                .build()).queue();
    }
}