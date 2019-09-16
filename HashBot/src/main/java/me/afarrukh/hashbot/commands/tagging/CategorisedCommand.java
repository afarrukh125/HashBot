package me.afarrukh.hashbot.commands.tagging;

import me.afarrukh.hashbot.commands.management.bot.CommandListCommand;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

/**
 * A command that is to be categorised by the command manager as having a certain types.
 * For example, subclasses of this interface can be music commands. In any case, this class, like the
 * AdminCommand interface, is checked by the command manager.
 * <p>
 * This is taken into account when displaying the lengthy list of commands. Users can type the category name
 * to the bot, and the command manager will return all commands with that category.
 *
 * @see MusicCommand
 * @see EconCommand
 * @see ExtrasCommand
 * @see RoleCommand
 * @see SystemCommand
 * @see me.afarrukh.hashbot.utils.EmbedUtils#getHelpMsg(MessageReceivedEvent, List)
 * @see CommandListCommand
 * @see me.afarrukh.hashbot.core.CommandManager
 */
public interface CategorisedCommand {

    default String getType() {
        if (this.getClass().getInterfaces().length > 1) {
            for (Class<?> itf : this.getClass().getInterfaces()) {
                if (itf.getSimpleName().equals("AdminCommand"))
                    continue;
                return itf.getSimpleName().replace("Command", "").toLowerCase();
            }
        }
        return getClass().getInterfaces()[0].getSimpleName().replace("Command", "").toLowerCase();
    }

}
