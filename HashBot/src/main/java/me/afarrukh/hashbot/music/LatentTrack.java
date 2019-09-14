package me.afarrukh.hashbot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * @author Abdullah
 * Created on 14/09/2019 at 14:28
 */
public class LatentTrack implements Runnable {

    private AudioTrack track;
    private int pos;
    private PlaylistLoader loader;

    public LatentTrack(AudioTrack track, int pos, PlaylistLoader loader) {
        this.track = track;
        this.pos = pos;
        this.loader = loader;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public int getPos() {
        return pos;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500);
            loader.addTrack(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
