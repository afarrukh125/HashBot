package me.afarrukh.hashbot.commands.management.bot;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.SystemCommand;
import me.afarrukh.hashbot.config.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CheckMemoryCommand extends Command implements SystemCommand {

    public CheckMemoryCommand() {
        super("checkmemory");
        addAlias("mem");
        description = "Displays memory used in megabytes.";
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        long memoryNow = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        long memoryDiff = memoryNow - Constants.INITIAL_MEMORY;
        memoryDiff /= (1024 * 1024); //Converting from bytes to kb to mb by dividing by 1024 twice

        evt.getChannel().sendMessage(new EmbedBuilder().setColor(Constants.EMB_COL)
                .appendDescription("Memory usage since startup is " + memoryDiff + "MB").build()).queue();

        if (memoryDiff > 30)
            System.gc();
    }
}
