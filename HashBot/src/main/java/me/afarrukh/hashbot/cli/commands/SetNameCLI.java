package me.afarrukh.hashbot.cli.commands;

import me.afarrukh.hashbot.cli.CLICommand;
import me.afarrukh.hashbot.core.Bot;

/**
 * @author Abdullah
 * Created on 16/09/2019 at 16:08
 *
 * Sets the name of the global JDA instance
 */
public class SetNameCLI extends CLICommand {

    public SetNameCLI() {
        super("setname");
        addAlias("sn");
    }

    @Override
    public void onInvocation(String params) {
        if(params == null) {
            System.out.println("You need to provide a username to set");
            return;
        }

        try {
            Bot.botUser.getSelfUser().getManager().setName(params).queue(
                    aVoid -> System.out.println("Global name changed to " + params),
                    throwable -> {
                System.out.println("Name change to " + params + " failed: " + throwable.getMessage());
            });
        } catch (Exception e) {
            System.out.println("Exception occurred " + e.getLocalizedMessage());
        }
    }
}
