package me.afarrukh.hashbot.gameroles;

public class GameRole {
    private final String creatorId;
    private final String name;

    public GameRole(String name, String creatorId) {
        this.name = name;
        this.creatorId = creatorId;
    }

    public String getName() {
        return name;
    }

    public String getCreator() { return creatorId; }
}
