package me.afarrukh.hashbot.music;

import javafx.scene.media.AudioTrack;
import net.dv8tion.jda.core.entities.Member;

import java.util.List;

/**
 * @author Abdullah
 * Created on 14/09/2019 at 14:24
 */
public class PlaylistLoader {

    private List<AudioTrack> tracks;
    private int maxSize;
    private Member member;

    public PlaylistLoader(List<AudioTrack> tracks, Member member, int maxSize) {
        this.tracks = tracks;
        this.maxSize = maxSize;
        this.member = member;
    }


}
