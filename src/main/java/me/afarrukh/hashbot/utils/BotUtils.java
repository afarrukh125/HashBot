package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class BotUtils {

    /**
     * Deletes the last message from a bot in the channel (Hopes that it is the current JDA Bot user)
     *
     * @param evt The event object containing the text channel so we can retrieve the text message
     */
    public static void deleteLastMsg(MessageReceivedEvent evt) {
        for (Message m : evt.getChannel().getIterableHistory()) {
            if (m.getAuthor().getId().equals(evt.getJDA().getSelfUser().getId())) {
                m.delete().queueAfter(1500, TimeUnit.MILLISECONDS);
                break;
            }
        }
    }

    public static boolean isPinnedChannel(MessageReceivedEvent evt) {
        GuildDataManager jgm = GuildDataMapper.getInstance().getDataManager(evt.getGuild());
        return evt.getChannel().getId().equals(jgm.getValue("pinnedchannel"));
    }

    /**
     * Calculates the approximate memory usage of the bot
     *
     * @return The approximate memory usage in megabytes
     * @see me.afarrukh.hashbot.cli.commands.CheckMemoryCLI
     * @see me.afarrukh.hashbot.commands.management.bot.CheckMemoryCommand
     */
    public static long getMemoryUsage() {
        long memoryNow =
                Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryDiff = memoryNow - Constants.INITIAL_MEMORY;
        memoryDiff /= (1024 * 1024); // Converting from bytes to kb to mb by dividing by 1024 twice

        return memoryDiff;
    }
}
