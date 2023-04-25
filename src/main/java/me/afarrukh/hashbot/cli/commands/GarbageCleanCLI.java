package me.afarrukh.hashbot.cli.commands;

import me.afarrukh.hashbot.cli.CLICommand;

public class GarbageCleanCLI extends CLICommand {
    public GarbageCleanCLI() {
        super("gc");
    }

    @Override
    public void onInvocation(String params) {
        System.gc();
        System.out.println("Ran System.gc() successfully");
    }
}
