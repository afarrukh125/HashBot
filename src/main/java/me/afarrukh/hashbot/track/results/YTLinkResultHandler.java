package me.afarrukh.hashbot.track.results;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YTLinkResultHandler extends YTGenericResultHandler {

    private final Database database;

    public YTLinkResultHandler(GuildAudioTrackManager gmm, MessageReceivedEvent evt, boolean playTop, Database database) {
        super(gmm, evt, playTop);
        this.database = database;
    }

    @Override
    public void noMatches() {
        evt.getChannel().sendMessage("Nothing found by that URL.").queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlistTracks) {

        // Load the playlist tracks into an array list, which we can optionally shuffle
        List<AudioTrack> playlist = new ArrayList<>(playlistTracks.getTracks());

        // Now we are checking whether the user chose to shuffle the list before adding it
        String[] tokens = evt.getMessage().getContentRaw().split(" ");
        if (tokens.length > 2) {
            if (tokens[2].equals("shuffle")) Collections.shuffle(playlist);
        }

        // We need the firstTrack to create the embed
        AudioTrack firstTrack = playlistTracks.getSelectedTrack();

        if (firstTrack == null) {
            firstTrack = playlist.get(0);
        }

        if (playlist.size() > Constants.MAX_PLAYLIST_SIZE) {
            evt.getChannel()
                    .sendMessage("Cannot queue playlist larger than " + Constants.MAX_PLAYLIST_SIZE)
                    .queue();
            return;
        }

        firstTrack.setUserData(evt.getAuthor().getName());
        AudioTrackUtils.play(evt, gmm, firstTrack, playTop, database);

        for (AudioTrack track : playlist) {
            if (track.equals(firstTrack)) continue;

            track.setUserData(evt.getAuthor().getName());
            gmm.getScheduler().queue(track);
        }

        evt.getChannel()
                .sendMessageEmbeds(EmbedUtils.getPlaylistEmbed(playlistTracks))
                .queue();
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        track.setUserData(evt.getAuthor().getName());

        AudioTrackUtils.play(evt, gmm, track, playTop, database);
    }
}
