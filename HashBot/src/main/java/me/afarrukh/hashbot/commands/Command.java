package me.afarrukh.hashbot.commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.*;

/**
 * A command is something the user types to interact with the bot.
 * This is usually prefixed with some character, in order for the bot to not call commands
 * unexpectedly, for example, during normal conversation in a discord text channel.
 * <p>
 * The default prefix for calling commands is !
 * Assuming this is the prefix, to call a command, for example, to check the latency (which returns the latency in ms between the server
 * that hosts this bot, and the discord servers), the "ping" command, can be called with
 *
 * <code>!ping</code>
 */
public abstract class Command {

    /**
     * The name of the command, i.e. what the user will need to type after the prefix to call it
     */
    private final String name;

    /***
     * The set of aliases, i.e. alternative ways to call this command
     * The set constraint prevents a command from having duplicate aliases, although, at the moment,
     * there is no way to prevent a command from having an alias the same as its original name
     */
    private final Set<String> aliases;

    /**
     * A mapping between a parameter that is to be provided to this command, and a helpful description to help
     * understand the purpose of the parameter.
     * <p>
     * A parameter for a command, can be defined as any additional space-separated
     * strings, that are to be used in the execution of the operation defined by said command.
     * <p>
     * For instance, if there was a command to ban, and assuming our command prefix is !
     * <p>
     * !ban foo
     * <p>
     * Will take into account the parameter 'foo' when banning a user. For example, it may be that the user
     * with user ID foo gets banned from the discord server that this <code>JDA</code> instance is in.
     * <p>
     * Note that the command name, <i>ban</i>, in this case, is not a parameter.
     */
    private final Map<String, String> parameters;

    /**
     * A helpful description to describe the purpose of this command.
     *
     * @see me.afarrukh.hashbot.utils.EmbedUtils#getHelpMsg(MessageReceivedEvent, List)
     */
    protected String description = null;

    /**
     * Create the command with the given name
     * <p>
     * This is primarily what all the commands differ by semantically. This is not to be confused with
     * the name of the class, although usually the name of the class typically ends up being either the
     * same as the command name, or very similar.
     * <p>
     * The command name is what the user types in order to invoke it. So in the example
     * <p>
     * !ping
     * <p>
     * 'ping' is the name of the command
     * <p>
     * Note that there can be aliases added to a command, which, obviously enough, are alternate ways to call the same
     * command. These are usually there for convenience.
     *
     * @param name The name of the command to be called.
     * @see #addAlias(String)
     */
    public Command(String name) {
        aliases = new HashSet<>();
        parameters = new LinkedHashMap<>(); // We need to preserve the order of insertion of the arguments
        this.name = name;
    }

    /**
     * Adds an alias to this command
     *
     * @param alias An alternative name for this command
     * @see #Command(String)
     */
    protected final void addAlias(String alias) {
        if(alias.equals(name))
            throw new IllegalArgumentException("You cannot have a command alias that is the same as the original command name");
        aliases.add(alias);
    }

    /**
     * Add a parameter, and a statement describing this parameter, to this command's parameter list
     *
     * @param parameter   The name of the parameter
     * @param description A meaningful description for the parameter
     * @see #parameters
     */
     protected final void addParameter(String parameter, String description) {
        parameters.put(parameter, description);
    }

    /**
     * Return an unmodifiable list of parameters for this command
     *
     * @return An unmodifiable list of parameters for this command
     */
    public final List<String> getParameters() {
        return Collections.unmodifiableList(new ArrayList<>(this.parameters.keySet()));
    }

    /**
     * Return the corresponding description for a given command
     * @param parameterName The name of the parameter to get the description for
     * @return The parameter description for the name of the parameter
     */
    public String getCommandDescription(String parameterName) {
        if(parameters.get(parameterName) == null)
            throw new IllegalArgumentException("Invalid parameter " + parameterName + " for " + this.getClass().getSimpleName());
        return parameters.get(parameterName);
    }

    /**
     * Specifies what the command must do when it is called
     *
     * @param evt    The event containing information about the command's invoker/channel
     * @param params The parameters for the command
     */
    public abstract void onInvocation(MessageReceivedEvent evt, String params);

    /**
     * Fired when the user enters an incorrect number of parameters and gives a message directing correct usage
     *
     * @param channel The TextChannel to send the message to with the correct usage message
     */
    public abstract void onIncorrectParams(TextChannel channel);

    /**
     * Obtain the name of this command
     *
     * @return The name of this command
     */
    public final String getName() {
        return name;
    }

    /**
     * Obtain the set of aliases for this command
     *
     * @return The set of aliases for this command
     */
    public final Set<String> getAliases() {
        return Collections.unmodifiableSet(aliases);
    }

    /**
     * Obtain the overall description for this command
     *
     * @return The string that contains a description of this command
     */
    public final String getDescription() {
        return description;
    }
}
