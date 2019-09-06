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
    public void playlistLoaded(AudioPlaylist playlist) {
        evt.getMessage().delete().queue();
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if(firstTrack == null) {
            firstTrack = playlist.getTracks().get(0);
        }

        if(playlist.getTracks().size() > Constants.MAX_PLAYLIST_SIZE) {
            evt.getChannel().sendMessage("Cannot queue playlist larger than " +Constants.MAX_PLAYLIST_SIZE).queue();
            return;
        }

        List<AudioTrack> playlistTracks = new ArrayList<>(playlist.getTracks());

        String[] tokens = evt.getMessage().getContentRaw().split(" ");
        // Checking flag provided by user-typed command
        if(tokens.length > 2) {
            if (tokens[2].equals("shuffle"))
                Collections.shuffle(playlistTracks);
        }

        firstTrack.setUserData(evt.getAuthor().getName());
        MusicUtils.play(evt, gmm, firstTrack, playTop);

        for(AudioTrack track: playlistTracks) {
            if(track.equals(firstTrack))
                continue;

            track.setUserData(evt.getAuthor().getName());
            gmm.getScheduler().queue(track);
        }

        evt.getChannel().sendMessage(EmbedUtils.getPlaylistEmbed(gmm, playlist)).queue();

    }

    @Override
    public void trackLoaded(AudioTrack track) {
        track.setUserData(evt.getAuthor().getName());

        MusicUtils.play(evt, gmm, track, playTop);
    }
}
