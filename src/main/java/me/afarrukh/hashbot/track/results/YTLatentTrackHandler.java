package me.afarrukh.hashbot.track.results;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.track.LatentTrack;
import me.afarrukh.hashbot.track.PlaylistLoader;
import net.dv8tion.jda.api.entities.Member;

public class YTLatentTrackHandler implements AudioLoadResultHandler {
    private final int idx;
    private final PlaylistLoader loader;
    private final Member member;

    public YTLatentTrackHandler(Member member, int idx, PlaylistLoader loader, String userId) {
        this.idx = idx;
        this.loader = loader;
        Member m = member.getGuild().getMemberById(userId);
        ;
        this.member = m != null ? m : member;
    }

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        audioTrack.setUserData(member.getUser().getName());
        LatentTrack track = new LatentTrack(audioTrack, idx, loader);
        new Thread(track).start();
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
    }

    @Override
    public void noMatches() {
    }

    @Override
    public void loadFailed(FriendlyException e) {
        loader.notifyFailed();
    }
}
