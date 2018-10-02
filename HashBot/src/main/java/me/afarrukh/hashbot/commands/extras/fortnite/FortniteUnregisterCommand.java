package me.afarrukh.hashbot.commands.extras.fortnite;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.ExtrasCommand;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class FortniteUnregisterCommand extends Command implements ExtrasCommand {

    public FortniteUnregisterCommand() {
        super("unregister");
        addAlias("unreg");
        addAlias("ftnunreg");

        description = "Removes a user from the list of fortnite users. No parameters.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        Bot.extrasManager.getGuildExtrasManager(evt.getGuild()).getFortniteExtra().removeUser(evt);
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
