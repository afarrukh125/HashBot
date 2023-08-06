package me.afarrukh.hashbot.track.results;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

abstract class YTGenericResultHandler implements AudioLoadResultHandler {

    final GuildAudioTrackManager gmm;
    final MessageReceivedEvent evt;
    final boolean playTop;
    protected final Database database;
    protected final String query;

    YTGenericResultHandler(GuildAudioTrackManager gmm, MessageReceivedEvent evt, boolean playTop, Database database) {
        this.gmm = gmm;
        this.evt = evt;
        this.playTop = playTop;
        this.database = database;
        String[] params = evt.getMessage().getContentRaw().split(" ", 2);
        this.query = params[1];

        if (!query.contains("list")) {
            evt.getChannel().sendMessage(":mag: **Searching**: `" + query + "`").queue();
        }
    }

    @Override
    public abstract void trackLoaded(AudioTrack audioTrack);

    @Override
    public abstract void playlistLoaded(AudioPlaylist audioPlaylist);

    @Override
    public abstract void noMatches();

    @Override
    public void loadFailed(FriendlyException e) {
        evt.getChannel()
                .sendMessage("Nothing found by `" + query + "` (" + e.getMessage() + ")")
                .queue();
    }
}
