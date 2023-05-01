package me.afarrukh.hashbot.commands.audiotracks.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import me.afarrukh.hashbot.track.PlaylistLoader;
import me.afarrukh.hashbot.track.results.YTFirstLatentTrackHandler;
import me.afarrukh.hashbot.track.results.YTLatentTrackHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

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
        Member member = evt.getMember();
        if (member.getVoiceState().getChannel() == null) {
            return;
        }

        if (params == null) {
            evt.getChannel().sendMessage("Please provide a playlist to load.").queue();
            return;
        }

        var database = Database.getInstance();

        //noinspection UnnecessaryLocalVariable
        var playlistName = params;
        var maybePlaylist = database.getPlaylistForUser(playlistName, member.getId());
        maybePlaylist.ifPresentOrElse(playlist -> {
            int playlistSize = playlist.getSize();
            var message = evt.getChannel()
                    .sendMessage("Queueing playlist %s with %d tracks. It might take a while for all tracks to be added to the queue.".formatted(params, playlistSize))
                    .complete();
            var playlistLoader = new PlaylistLoader(member, playlistSize, message, params);

            AudioPlayerManager playerManager = Bot.trackManager.getPlayerManager();
            GuildAudioTrackManager guildAudioPlayer = Bot.trackManager.getGuildAudioPlayer(evt.getGuild());
            var iterator = playlist.getItems().iterator();
            var firstItem = iterator.next();
            playerManager.loadItemOrdered(guildAudioPlayer, firstItem.uri(), new YTFirstLatentTrackHandler(member, firstItem.userId()));
            int idx = 0;
            while (iterator.hasNext()) {
                var nextItem = iterator.next();
                playerManager.loadItemOrdered(guildAudioPlayer, nextItem.uri(), new YTLatentTrackHandler(member, idx++, playlistLoader, nextItem.userId()));
            }
        }, sendPlaylistNotFoundMessage(evt, playlistName));

    }

    @NotNull
    private Runnable sendPlaylistNotFoundMessage(MessageReceivedEvent evt, String playlistName) {
        return () -> evt.getChannel().sendMessage("Could not find a playlist with name %s".formatted(playlistName)).queue();
    }
}
