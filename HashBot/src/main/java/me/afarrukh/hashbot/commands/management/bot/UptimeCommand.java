package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.utils.CmdUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class UptimeCommand extends Command {

    public UptimeCommand() {
        super("uptime", new String[]{"up"});
        description = "Displays the bot uptime in HH:MM:SS";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        long now = System.currentTimeMillis();
        long upTimeUnix = now - Constants.timeStarted;
        long seconds =  (int) (upTimeUnix / 1000) % 60;
        int minutes =   (int) (upTimeUnix / 1000 / 60) % 60;
        int hours =     (int) (upTimeUnix / 1000 / 60 / 60) % 24;
        int days =      (int) (upTimeUnix / 1000 / 60 / 60) / 24;

        evt.getTextChannel().sendMessage(new EmbedBuilder()
                .setColor(Constants.EMB_COL)
                .appendDescription("Bot uptime: " + days + " days, "
                        + hours + " hours, " + minutes + " minutes and " + seconds + " seconds.")
                .build()).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
