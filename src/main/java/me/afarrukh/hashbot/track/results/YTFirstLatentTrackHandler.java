package me.afarrukh.hashbot.track.results;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.entities.Member;

public class YTFirstLatentTrackHandler implements AudioLoadResultHandler {
    private final Member member;

    public YTFirstLatentTrackHandler(Member member, String userId) {
        Member m = member.getGuild().getMemberById(userId);
        this.member = m != null ? m : member;
    }

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        audioTrack.setUserData(member.getUser().getName());
        Bot.trackManager.getGuildAudioPlayer(member.getGuild()).getScheduler().queue(audioTrack);
        AudioTrackUtils.connectToChannel(member);
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
