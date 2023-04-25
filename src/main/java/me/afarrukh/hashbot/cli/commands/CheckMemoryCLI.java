package me.afarrukh.hashbot.cli.commands;

import me.afarrukh.hashbot.cli.CLICommand;
import me.afarrukh.hashbot.utils.BotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckMemoryCLI extends CLICommand {
    private static final Logger LOG = LoggerFactory.getLogger(CheckMemoryCLI.class);

    public CheckMemoryCLI() {
        super("mem");
    }

    @Override
    public void onInvocation(String params) {
        long memory = BotUtils.getMemoryUsage();
        LOG.info("Memory usage since startup is {}MB", memory);
    }
}
