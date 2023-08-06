package me.afarrukh.hashbot.track.results;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class YTSearchResultHandler extends YTGenericResultHandler {


    public YTSearchResultHandler(GuildAudioTrackManager gmm, MessageReceivedEvent evt, boolean playTop, Database database) {
        super(gmm, evt, playTop, database);
    }

    @Override
    public void noMatches() {
        evt.getChannel().sendMessage("Nothing found by " + query).queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if (firstTrack == null) {
            firstTrack = playlist.getTracks().get(0);
        }

        firstTrack.setUserData(evt.getAuthor().getName());

        AudioTrackUtils.play(evt, gmm, firstTrack, playTop,database);
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        evt.getChannel()
                .sendMessageEmbeds(EmbedUtils.getQueuedEmbed(gmm, track, evt, database))
                .queue();

        track.setUserData(evt.getAuthor().getName());
        AudioTrackUtils.play(evt, gmm, track, playTop, database);
    }
}
