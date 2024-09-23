package me.afarrukh.hashbot.track;

import static java.util.Collections.unmodifiableList;

import java.util.List;

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
