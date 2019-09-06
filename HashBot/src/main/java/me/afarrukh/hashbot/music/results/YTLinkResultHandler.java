package me.afarrukh.hashbot.music.results;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.music.GuildMusicManager;
import me.afarrukh.hashbot.utils.EmbedUtils;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YTLinkResultHandler extends YTGenericResultHandler {

    public YTLinkResultHandler(GuildMusicManager gmm, MessageReceivedEvent evt, boolean playTop) {
        super(gmm, evt, playTop);
    }

    @Override
    public void noMatches() {
        evt.getChannel().sendMessage("Nothing found by that URL.").queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlistTracks) {
        evt.getMessage().delete().queue();

        // Load the playlist tracks into an array list, which we can optionally shuffle
        List<AudioTrack> playlist = new ArrayList<>(playlistTracks.getTracks());

        // Now we are checking whether the user chose to shuffle the list before adding it
        String[] tokens = evt.getMessage().getContentRaw().split(" ");
        if(tokens.length > 2) {
            if (tokens[2].equals("shuffle"))
                Collections.shuffle(playlist);
        }

        // We need the firstTrack to create the embed
        AudioTrack firstTrack = playlistTracks.getSelectedTrack();

        if(firstTrack == null) {
            firstTrack = playlist.get(0);
        }

        if(playlist.size() > Constants.MAX_PLAYLIST_SIZE) {
            evt.getChannel().sendMessage("Cannot queue playlist larger than " +Constants.MAX_PLAYLIST_SIZE).queue();
            return;
        }

        firstTrack.setUserData(evt.getAuthor().getName());
        MusicUtils.play(evt, gmm, firstTrack, playTop);

        for(AudioTrack track: playlist) {
            if(track.equals(firstTrack))
                continue;

            track.setUserData(evt.getAuthor().getName());
            gmm.getScheduler().queue(track);
        }

        evt.getChannel().sendMessage(EmbedUtils.getPlaylistEmbed(gmm, playlistTracks)).queue();

    }

    @Override
    public void trackLoaded(AudioTrack track) {
        track.setUserData(evt.getAuthor().getName());

        MusicUtils.play(evt, gmm, track, playTop);
    }
}
