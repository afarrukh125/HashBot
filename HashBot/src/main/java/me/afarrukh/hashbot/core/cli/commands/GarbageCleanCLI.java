package me.afarrukh.hashbot.core.cli.commands;

import me.afarrukh.hashbot.core.cli.CLICommand;

/**
 * @author Abdullah
 * Created on 06/09/2019 at 18:39
 * Essentially runs System.gc();
 */
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
