package me.afarrukh.hashbot.core.cli.commands;

import me.afarrukh.hashbot.core.cli.CLICommand;
import me.afarrukh.hashbot.utils.BotUtils;

/**
 * @author Abdullah
 * Created on 06/09/2019 at 18:39
 * A command line command to check for memory
 */
public class CheckMemoryCLI extends CLICommand {

    public CheckMemoryCLI() {
        super("mem");
    }

    @Override
    public void onInvocation(String params) {
        System.out.println("Memory usage since startup is " + BotUtils.getMemoryUsage() + "MB.");
    }
}
