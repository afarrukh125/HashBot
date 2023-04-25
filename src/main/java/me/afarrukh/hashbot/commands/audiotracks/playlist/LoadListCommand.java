package me.afarrukh.hashbot.commands.audiotracks.playlist;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.data.SQLUserDataManager;
import me.afarrukh.hashbot.exceptions.PlaylistException;
import me.afarrukh.hashbot.track.PlaylistLoader;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;

public class LoadListCommand extends Command implements AudioTrackCommand {

    public LoadListCommand() {
        super("loadlist");
        addAlias("plist");
        addAlias("dlist");

        description = "Load a playlist by name from your selection";

        addParameter("list name", "The name of the playlist to be loaded");
        addExampleUsage("loadlist list");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (Objects.requireNonNull(Objects.requireNonNull(evt.getMember()).getVoiceState())
                        .getChannel()
                == null) return;

        if (params == null) {
            evt.getChannel().sendMessage("Please provide a playlist to load.").queue();
            return;
        }

        SQLUserDataManager dataManager = new SQLUserDataManager(evt.getMember());

        Message message = null;
        try {
            final int listSize = dataManager.getPlaylistSize(params);
            message = evt.getChannel()
                    .sendMessage("Queueing playlist " + params + " with " + listSize + " tracks."
                            + " It might take a while for all tracks to be added to the queue.")
                    .complete();
            PlaylistLoader loader = new PlaylistLoader(evt.getMember(), listSize, message, params);
            dataManager.loadPlaylistByName(params, loader);

        } catch (PlaylistException e) {
            if (message != null)
                message.editMessage("Could not find a playlist with that name").queue();
        }
    }
}
