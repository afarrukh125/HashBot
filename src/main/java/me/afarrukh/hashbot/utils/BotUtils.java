package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Optional;

import static java.lang.Runtime.getRuntime;

public class BotUtils {

    public static boolean isPinnedChannel(Database database, MessageReceivedEvent evt) {
        Optional<String> pinnedChannelIdForGuild =
                database.getPinnedChannelIdForGuild(evt.getGuild().getId());
        return pinnedChannelIdForGuild
                .filter(s -> evt.getChannel().getId().equals(s))
                .isPresent();
    }

    /**
     * Calculates the approximate memory usage of the bot
     *
     * @return The approximate memory usage in megabytes
     * @see me.afarrukh.hashbot.cli.commands.CheckMemoryCLI
     * @see me.afarrukh.hashbot.commands.management.bot.CheckMemoryCommand
     */
    public static long getMemoryUsage() {
        long memoryNow = getRuntime().totalMemory() - getRuntime().freeMemory();
        long memoryDiff = memoryNow - Constants.INITIAL_MEMORY;
        memoryDiff /= (1024 * 1024); // Converting from bytes to kb to mb by dividing by 1024 twice

        return memoryDiff;
    }
}
