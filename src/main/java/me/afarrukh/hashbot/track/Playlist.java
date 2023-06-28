package me.afarrukh.hashbot.track;

import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Playlist {

    private final String name;
    private final List<PlaylistItem> tracks;
    private final int size;

    public Playlist(String name, List<PlaylistItem> tracks) {
        this.name = name;
        this.tracks = tracks;
        this.size = tracks.size();
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public List<PlaylistItem> getItems() {
        return unmodifiableList(tracks);
    }
}
