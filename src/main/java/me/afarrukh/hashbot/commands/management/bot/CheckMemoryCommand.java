package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.SystemCommand;
import me.afarrukh.hashbot.config.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static java.lang.Runtime.getRuntime;

public class CheckMemoryCommand extends Command implements SystemCommand {

    public CheckMemoryCommand() {
        super("checkmemory");
        addAlias("mem");
        description = "Displays memory used in megabytes.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        long memoryDiff = getMemoryDifferenceFromStartup();

        evt.getChannel()
                .sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Constants.EMB_COL)
                        .appendDescription("Memory usage since startup is " + memoryDiff + "MB")
                        .build())
                .queue();

        if (memoryDiff > 30) System.gc();
    }

    public static long getMemoryDifferenceFromStartup() {
        long memoryNow = getRuntime().totalMemory() - getRuntime().freeMemory();

        long memoryDiff = memoryNow - Constants.INITIAL_MEMORY;
        memoryDiff /= (1024 * 1024); // Converting from bytes to kb to mb by dividing by 1024 twice
        return memoryDiff;
    }
}
