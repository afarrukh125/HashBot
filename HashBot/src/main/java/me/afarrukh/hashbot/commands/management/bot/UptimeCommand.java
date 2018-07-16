package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.utils.CmdUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class UptimeCommand extends Command {

    public UptimeCommand() {
        super("uptime");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        long now = System.currentTimeMillis();
        long upTimeUnix = now - Constants.timeStarted;

        evt.getTextChannel().sendMessage(new EmbedBuilder()
                .setColor(Constants.EMB_COL)
                .appendDescription("Bot uptime: " + CmdUtils.longToHHMMSS(upTimeUnix))
                .build()).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
