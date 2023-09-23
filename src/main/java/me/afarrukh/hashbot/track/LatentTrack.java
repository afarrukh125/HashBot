package me.afarrukh.hashbot.track;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class LatentTrack implements Runnable {

    private final AudioTrack track;

    private final int pos;

    private final PlaylistLoader loader;

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
            loader.addTrack(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
