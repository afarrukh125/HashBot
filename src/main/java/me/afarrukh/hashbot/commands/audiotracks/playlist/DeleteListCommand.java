package me.afarrukh.hashbot.commands.audiotracks.playlist;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.SQLUserDataManager;
import me.afarrukh.hashbot.exceptions.PlaylistException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DeleteListCommand extends Command implements AudioTrackCommand {

    public DeleteListCommand() {
        super("deletelist");
        addAlias("dl");
        description = "Delete one of your playlists by name";
        addParameter("name", "The name of the playlist to be deleted");
        addExampleUsage("deletelist list");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        if (params == null) {
            evt.getChannel()
                    .sendMessage("You need to provide the name of the playlist you would like to delete." + "Use "
                            + Bot.prefixManager
                                    .getGuildRoleManager(evt.getGuild())
                                    .getPrefix() + new ViewListCommand().getName()
                            + " to view your playlists.")
                    .queue();
            return;
        }

        SQLUserDataManager dataManager = new SQLUserDataManager(evt.getMember());

        try {
            int listSize = dataManager.getPlaylistSize(params);
            dataManager.deletePlaylist(params);
            evt.getChannel()
                    .sendMessage("Successfully deleted the playlist " + params + " with " + listSize + " tracks")
                    .queue();
        } catch (PlaylistException e) {
            evt.getChannel()
                    .sendMessage("There is no playlist with the name " + params + ".")
                    .queue();
        }
    }
}
