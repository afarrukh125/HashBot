package me.afarrukh.hashbot.track;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Playlist {

    private final String name;
    private final int size;
    private final List<PlaylistItem> tracks;

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

    public List<PlaylistItem> getItems() {
        return unmodifiableList(tracks);
    }
}
