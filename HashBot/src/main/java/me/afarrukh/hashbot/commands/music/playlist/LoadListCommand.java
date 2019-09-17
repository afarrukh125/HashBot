package me.afarrukh.hashbot.commands.music.playlist;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.data.SQLUserDataManager;
import me.afarrukh.hashbot.exceptions.PlaylistException;
import me.afarrukh.hashbot.music.PlaylistLoader;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Abdullah
 * Created on 10/09/2019 at 22:59
 */
public class LoadListCommand extends Command implements MusicCommand {

    public LoadListCommand() {
        super("loadlist");
        addAlias("plist");
        addAlias("dlist");

        description = "Load a playlist by name from your selection";

        addParameter("list name", "The name of the playlist to be loaded");
        setExampleUsage("loadlist list");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        SQLUserDataManager dataManager = new SQLUserDataManager(evt.getMember());

        Message message = null;
        try {
            final int listSize = dataManager.getPlaylistSize(params);
            message = evt.getTextChannel().sendMessage("Queueing playlist " + params + " with " + listSize + " tracks." +
                    " It might take a while for all tracks to be added to the queue.").complete();
            PlaylistLoader loader = new PlaylistLoader(evt.getMember(), listSize, message, params);
            dataManager.loadPlaylistByName(params, loader);

        } catch (PlaylistException e) {
            if(message != null)
                message.editMessage("Could not find a playlist with that name").queue();
        }
    }
}
