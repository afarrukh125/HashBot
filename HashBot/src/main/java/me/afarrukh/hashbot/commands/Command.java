package me.afarrukh.hashbot.commands;

import me.afarrukh.hashbot.utils.MusicUtils;
import me.afarrukh.hashbot.utils.UserUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class Command {

    /**
     * The name of the command, i.e. what the user will need to type after the prefix to call it
     */
    protected String name;
    protected String[] aliases;

    public Command(String name, String[] aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public Command(String name) {
        this.name = name;
        aliases = null;
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

    public String[] getAliases() {
        return aliases;
    }
}
