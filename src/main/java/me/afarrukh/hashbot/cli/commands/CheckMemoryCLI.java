package me.afarrukh.hashbot.cli.commands;

import me.afarrukh.hashbot.cli.CLICommand;
import me.afarrukh.hashbot.utils.BotUtils;

public class CheckMemoryCLI extends CLICommand {

    public CheckMemoryCLI() {
        super("mem");
    }

    @Override
    public void onInvocation(String params) {
        System.out.println("Memory usage since startup is " + BotUtils.getMemoryUsage() + "MB.");
    }
}
