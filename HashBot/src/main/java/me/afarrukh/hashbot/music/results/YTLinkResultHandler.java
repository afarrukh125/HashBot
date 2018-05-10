package me.afarrukh.hashbot.music.results;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.music.GuildMusicManager;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class YTLinkResultHandler extends YTGenericResultHandler {

    private GuildMusicManager gmm;
    private MessageReceivedEvent evt;

    public YTLinkResultHandler(GuildMusicManager gmm, MessageReceivedEvent evt) {
        super(gmm, evt);
    }

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {

    }

    @Override
    public void noMatches() {

    }

    @Override
    public void loadFailed(FriendlyException e) {

    }
}
