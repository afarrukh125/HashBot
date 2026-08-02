package me.afarrukh.hashbot.commands.audiotracks.playlist;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.track.Playlist;
import me.afarrukh.hashbot.track.PlaylistQueuer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class LoadListCommand extends Command implements AudioTrackCommand {

    private final Database database;
    private final PlaylistQueuer playlistQueuer;

    public LoadListCommand(Database database, AudioTrackManager audioTrackManager) {
        super("loadlist");
        this.database = database;
        this.playlistQueuer = new PlaylistQueuer(audioTrackManager);
        addAlias("plist");
        addAlias("dlist");

        description = "Load a playlist by name from your selection";

        addParameter("list name", "The name of the playlist to be loaded");
        addExampleUsage("loadlist list");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        Member member = evt.getMember();
        var voiceState = member.getVoiceState();
        if (voiceState == null || voiceState.getChannel() == null) {
            evt.getChannel()
                    .sendMessage("You must be in a voice channel to load a playlist")
                    .queue();
            return;
        }

        if (params == null) {
            evt.getChannel().sendMessage("Please provide a playlist to load.").queue();
            return;
        }

        database.getPlaylistForUser(params, member.getId())
                .ifPresentOrElse(
                        playlist -> startQueueing(evt, member, playlist), sendPlaylistNotFoundMessage(evt, params));
    }

    private void startQueueing(MessageReceivedEvent evt, Member member, Playlist playlist) {
        evt.getChannel()
                .sendMessage(
                        "Queueing playlist %s with %d tracks. It might take a while for all tracks to be added to the queue."
                                .formatted(playlist.getName(), playlist.getSize()))
                .queue(status -> playlistQueuer.queue(playlist, member, status));
    }

    @NotNull
    private Runnable sendPlaylistNotFoundMessage(MessageReceivedEvent evt, String playlistName) {
        return () -> evt.getChannel()
                .sendMessage("Could not find a playlist with name %s".formatted(playlistName))
                .queue();
    }
}
