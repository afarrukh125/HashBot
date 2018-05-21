package me.afarrukh.hashbot.gameroles;

import net.dv8tion.jda.core.entities.User;

import java.awt.*;

public class GameRole {
    private String creatorId;
    private String name;

    public GameRole(String name, String creatorId) {
        this.name = name;
        this.creatorId = creatorId;
    }

    public String getName() {
        return name;
    }

    public String getCreator() { return creatorId; }
}
