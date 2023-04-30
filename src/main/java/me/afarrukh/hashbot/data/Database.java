package me.afarrukh.hashbot.data;

import me.afarrukh.hashbot.track.Playlist;

public interface Database {

    public static Database getInstance() {
        return new Neo4jDatabase();
    }

    Playlist getPlaylistForUser(String playlistName, String userId);
}
