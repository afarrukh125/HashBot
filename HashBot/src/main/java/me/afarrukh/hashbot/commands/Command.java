package me.afarrukh.hashbot.commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.HashSet;
import java.util.Set;

public abstract class Command {

    /**
     * The name of the command, i.e. what the user will need to type after the prefix to call it
     */
    private final String name;
    private final Set<String> aliases = new HashSet<>();
    protected String description = null;

    public Command(String name) {
        this.name = name;
    }

    protected void addAlias(String s) {
        aliases.add(s);
    }

    /**
     * Specifies what the command must do when it is called
     * @param evt The event containing information about the command's invoker/channel
     * @param params The parameters for the command
     */
    public abstract void onInvocation(MessageReceivedEvent evt, String params);

    /**
     * Fired when the user enters an incorrect number of parameters and gives a message directing correct usage
     * @param channel The TextChannel to send the message to with the correct usage message
     */
    public abstract void onIncorrectParams(TextChannel channel);

    public String getName() {
        return name;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }
}
