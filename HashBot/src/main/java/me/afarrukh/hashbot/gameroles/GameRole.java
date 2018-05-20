package me.afarrukh.hashbot.gameroles;

import java.awt.*;

public class GameRole {
    private Color color;
    private String name;

    public GameRole(String name, long red, long blue, long green) {
        this.name = name;
        this.color = new Color((int) red, (int) blue, (int) green);
    }
}
