package me.afarrukh.hashbot.music.results;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.music.PlaylistLoader;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.Member;

/**
 * @author Abdullah
 * Created on 14/09/2019 at 16:02
 * <p>
 * Used to load the first track in the <code>PlaylistLoader</code> only
 * This class was only made to clean up the code in <code>SQLUserDataManager</code>
 * @see me.afarrukh.hashbot.data.SQLUserDataManager#loadPlaylistByName(String, PlaylistLoader)
 */
public class YTFirstLatentTrackHandler implements AudioLoadResultHandler {
    private Member member;

    public YTFirstLatentTrackHandler(Member member) {
        this.member = member;
    }

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        audioTrack.setUserData(member.getUser().getName());
        Bot.musicManager.getGuildAudioPlayer(member.getGuild()).getScheduler().queue(audioTrack);
        MusicUtils.connectToChannel(member);
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
