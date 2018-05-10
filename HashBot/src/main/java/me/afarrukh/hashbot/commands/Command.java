package me.afarrukh.hashbot.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class Command {

    /**
     * The name of the command, i.e. what the user will need to type after the prefix to call it
     */
    private String name;

    public Command(String name) {
        this.name = name;
    }

    /**
     * Specifies what the command must do when it is called
     * @param evt The event containing information about the command's invoker/channel
     * @param params The parameters for the command
     */
    public abstract void onInvocation(MessageReceivedEvent evt, String params);

    public String getName() {
        return name;
    }
}
