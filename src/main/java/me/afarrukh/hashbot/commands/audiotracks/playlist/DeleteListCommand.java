package me.afarrukh.hashbot.commands.audiotracks.playlist;

import static java.util.Objects.requireNonNull;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.data.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DeleteListCommand extends Command implements AudioTrackCommand {

    private final Database database;

    public DeleteListCommand(Database database) {
        super("deletelist");
        this.database = database;
        addAlias("dl");
        description = "Delete one of your playlists by name";
        addParameter("name", "The name of the playlist to be deleted");
        addExampleUsage("deletelist list");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        var prefix = database.getPrefixForGuild(evt.getGuild().getId());
        var viewListCommandName = new ViewListCommand(database).getName();
        if (params == null) {
            evt.getChannel()
                    .sendMessage(("You need to provide the name of the playlist you would like to delete. "
                                    + "Use %s%s to view your playlists.")
                            .formatted(prefix, viewListCommandName))
                    .queue();
            return;
        }

        var playlistName = params;

        String memberId = requireNonNull(evt.getMember()).getId();
        database.getPlaylistForUser(playlistName, memberId)
                .ifPresentOrElse(
                        playlist -> {
                            var listSize = playlist.getSize();
                            database.deletePlaylistForUser(playlistName, memberId);
                            evt.getChannel()
                                    .sendMessage("Successfully deleted the playlist " + playlistName + " with "
                                            + listSize + " tracks")
                                    .queue();
                        },
                        () -> evt.getChannel()
                                .sendMessage("There is no playlist with the name " + playlistName + ".")
                                .queue());
    }
}
