package me.afarrukh.hashbot.commands.tagging;

import me.afarrukh.hashbot.commands.management.bot.CommandListCommand;

/**
 * A command that is to be categorised by the command manager as having a certain types.
 * For example, subclasses of this interface can be track commands. In any case, this class, like the
 * AdminCommand interface, is checked by the command manager.
 * <p>
 * This is taken into account when displaying the lengthy list of commands. Users can type the category name
 * to the bot, and the command manager will return all commands with that category.
 *
 * @see AudioTrackCommand
 * @see SystemCommand
 * @see CommandListCommand
 * @see me.afarrukh.hashbot.core.CommandManager
 */
public interface CategorisedCommand {

    default String getType() {
        if (this.getClass().getInterfaces().length > 1) {
            for (Class<?> itf : this.getClass().getInterfaces()) {
                if (itf.getSimpleName().equals("AdminCommand")) continue;
                return itf.getSimpleName().replace("Command", "").toLowerCase();
            }
        }
        return getClass()
                .getInterfaces()[0]
                .getSimpleName()
                .replace("Command", "")
                .toLowerCase();
    }
}
