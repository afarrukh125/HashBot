package me.afarrukh.hashbot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * @author Abdullah
 * Created on 14/09/2019 at 14:28
 */
public class LatentTrack {

    private AudioTrack track;
    private int pos;

    public LatentTrack(AudioTrack track, int pos) {
        this.track = track;
        this.pos = pos;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public int getPos() {
        return pos;
    }
}
