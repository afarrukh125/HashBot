package me.afarrukh.hashbot.music.results;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.music.GuildMusicManager;
import me.afarrukh.hashbot.utils.EmbedUtils;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class YTSearchResultHandler extends YTGenericResultHandler {

    public YTSearchResultHandler(GuildMusicManager gmm, MessageReceivedEvent evt, boolean playTop) {
        super(gmm, evt, playTop);
    }

    @Override
    public void noMatches() {
        evt.getChannel().sendMessage("Nothing found by that query").queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if (firstTrack == null) {
            firstTrack = playlist.getTracks().get(0);
        }

        firstTrack.setUserData(evt.getAuthor().getName());

        MusicUtils.play(evt, gmm, firstTrack, playTop);
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        evt.getChannel().sendMessage(EmbedUtils.getQueuedEmbed(gmm, track, evt)).queue();

        track.setUserData(evt.getAuthor().getName());
        MusicUtils.play(evt, gmm, track, playTop);
    }
}
