package me.afarrukh.hashbot.commands;

import java.util.*;
import me.afarrukh.hashbot.data.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A command is something the user types to interact with the bot.
 * This is usually prefixed with some character, in order for the bot to not call commands
 * unexpectedly, for example, during normal conversation in a discord text channel.
 * <p>
 * The default prefix for calling commands is !
 * Assuming this is the prefix, then to call a command, for example to check the latency (which returns the latency in ms between the server
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
     * A command may have an example usage to show, when querying the details for it
     */
    private final Set<String> exampleUsages;

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
    protected Command(String name) {
        aliases = new HashSet<>();
        parameters = new LinkedHashMap<>(); // We need to preserve the order of insertion of the arguments
        this.name = name;
        exampleUsages = new LinkedHashSet<>();
    }

    /**
     * Adds an alias to this command
     *
     * @param alias An alternative name for this command
     * @see #Command(String)
     */
    protected final void addAlias(String alias) {
        if (alias.equals(name))
            throw new IllegalArgumentException(
                    "You cannot have a command alias that is the same as the original command name");
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
        String param = processParameter(parameter);
        parameters.put(param, description);
    }

    /**
     * Capitalise the first name of the parameter
     *
     * @param parameter The name of the parameter
     * @return A string that has the parameter formatted correctly
     */
    private String processParameter(String parameter) {
        StringBuilder sb = new StringBuilder();
        for (String param : parameter.split(" ")) {
            sb.append(param.substring(0, 1).toUpperCase())
                    .append(param.length() > 1 ? param.substring(1) : "")
                    .append(" ");
        }
        return sb.toString().trim();
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
    protected void onIncorrectParams(Database database, TextChannel channel) {
        channel.sendMessageEmbeds(getCommandHelpMessage(database, channel)).queue();
    }

    public final MessageEmbed getCommandHelpMessage(Database database, TextChannel channel) {
        var prefix = database.getPrefixForGuild(channel.getGuild().getId());
        var embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Viewing help for " + this.name);
        var stringBuilder = new StringBuilder();
        stringBuilder.append("**Description:** ").append(this.description).append("\n\n");

        if (!aliases.isEmpty()) {
            stringBuilder.append("**Aliases:**\n");
            for (String alias : aliases) {
                stringBuilder.append("- ").append(alias).append("\n");
            }
            stringBuilder.append("\n");
        }
        if (!parameters.keySet().isEmpty()) {
            stringBuilder.append("**Parameters:** \n");
            for (String parameter : parameters.keySet())
                stringBuilder
                        .append("**")
                        .append("- ")
                        .append(parameter)
                        .append(":** ")
                        .append(parameters.get(parameter))
                        .append("\n\n");
            stringBuilder
                    .append("**Template usage:** ")
                    .append(prefix)
                    .append(name)
                    .append(" ");
            for (String param : parameters.keySet())
                stringBuilder.append("<").append(param).append("> ");
            stringBuilder.append("\n\n");
            if (!exampleUsages.isEmpty()) {
                stringBuilder.append("**Example usages:** \n");
                for (String example : exampleUsages) {
                    stringBuilder.append("- ").append(prefix).append(example).append("\n");
                }
            }
        }
        return embedBuilder.setDescription(stringBuilder).build();
    }

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

    /**
     * Returns the set of example strings for this command
     *
     * @param exampleUsage A string that represents an example usage for this command
     */
    protected final void addExampleUsage(String exampleUsage) {
        exampleUsages.add(exampleUsage);
    }

    /**
     * Obtain the set of example usages
     *
     * @return The set of all example usages for this command
     */
    public final Set<String> getExampleUsages() {
        return Collections.unmodifiableSet(exampleUsages);
    }
}
