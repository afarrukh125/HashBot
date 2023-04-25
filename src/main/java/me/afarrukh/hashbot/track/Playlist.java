package me.afarrukh.hashbot.track;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Playlist {

    private final String name;
    private final int size;
    private final List<AudioTrack> tracks;

    public Playlist(String name, int size) {
        tracks = new ArrayList<>();
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public void addTrack(AudioTrack t) {
        tracks.add(t);
    }

    public void removeTrack(AudioTrack t) {
        tracks.remove(t);
    }

    public List<AudioTrack> getTracks() {
        return Collections.unmodifiableList(tracks);
    }
}
