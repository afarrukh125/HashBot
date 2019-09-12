package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.data.SQLUserDataManager;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Abdullah
 * Created on 11/09/2019 at 23:08
 */
public class ViewListCommand extends Command {

    public ViewListCommand() {
        super("viewlists");
        addAlias("vl");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        new SQLUserDataManager(evt.getMember()).viewAllPlaylists();

    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
