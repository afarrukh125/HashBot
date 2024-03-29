package me.afarrukh.hashbot.track;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class LatentTrack implements Runnable {

    /**
     * The associated audio track with this object
     */
    private final AudioTrack track;

    /**
     * The position of this audio track in the loader
     */
    private final int pos;

    /**
     * The playlist loader that serves as the "barrier" for this track, and tracks
     * that are part of the same playlist
     */
    private final PlaylistLoader loader;

    /**
     * Instantiates this object
     *
     * @param track  The track to be wrapped
     * @param pos    The position of this track
     * @param loader The associated <code>PlaylistLoader</code>
     */
    public LatentTrack(AudioTrack track, int pos, PlaylistLoader loader) {
        this.track = track;
        this.pos = pos;
        this.loader = loader;
    }

    /**
     * Return the wrapped track
     *
     * @return The internal AudioTrack object
     */
    public AudioTrack getTrack() {
        return track;
    }

    /**
     * @return The position of this track in the <code>PlaylistLoader</code>
     */
    public int getPos() {
        return pos;
    }

    /**
     * Add the track to the playlist loader
     */
    @Override
    public void run() {
        try {
            loader.addTrack(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
