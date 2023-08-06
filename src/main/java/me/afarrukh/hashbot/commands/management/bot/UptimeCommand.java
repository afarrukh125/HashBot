package me.afarrukh.hashbot.commands.management.bot;

import com.google.inject.Guice;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.SystemCommand;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.CommandManager;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.data.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UptimeCommand extends Command implements SystemCommand {

    public UptimeCommand(Database database) {
        super("uptime", database);
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

        var injector = Guice.createInjector(new CoreBotModule());
        evt.getChannel()
                .sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Constants.EMB_COL)
                        .appendDescription("Bot uptime: " + days + " days, " + hours + " hours, " + minutes
                                + " minutes and " + seconds + " seconds.\n\n")
                        .appendDescription("There have been "
                                + injector.getInstance(CommandManager.class).getCommandCount()
                                + " commands executed this session.")
                        .build())
                .queue();
    }
}
