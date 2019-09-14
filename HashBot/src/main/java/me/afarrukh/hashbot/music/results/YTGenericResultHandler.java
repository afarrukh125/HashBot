package me.afarrukh.hashbot.music.results;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.music.GuildMusicManager;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

abstract class YTGenericResultHandler implements AudioLoadResultHandler {

    final GuildMusicManager gmm;
    final MessageReceivedEvent evt;
    final boolean playTop;
    private final String query;

    YTGenericResultHandler(GuildMusicManager gmm, MessageReceivedEvent evt, boolean playTop) {
        this.gmm = gmm;
        this.evt = evt;
        this.playTop = playTop;
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
        evt.getChannel().sendMessage("Nothing found by `" + query + "`").queue();
    }
}
