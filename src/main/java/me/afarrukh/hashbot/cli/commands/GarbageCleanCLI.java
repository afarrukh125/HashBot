package me.afarrukh.hashbot.cli.commands;

import me.afarrukh.hashbot.cli.CLICommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GarbageCleanCLI extends CLICommand {
    private static final Logger LOG = LoggerFactory.getLogger(GarbageCleanCLI.class);

    public GarbageCleanCLI() {
        super("gc");
    }

    @Override
    public void onInvocation(String params) {
        System.gc();
        LOG.info("Ran System.gc() successfully");
    }
}
