package me.afarrukh.hashbot.config;

import java.awt.*;

public class Constants {
    public static final int MAX_PLAYLIST_SIZE = 400;

    // The amount of seconds to wait before disconnecting after a user leaves
    public static final int DISCONNECT_DELAY = 60 * 10;
    public static final int AudioTrackBAR_SCALE = 35;

    // The maximum volume the bot can play at.
    public static final int MAX_VOL = 100;
    public static final String SELECTEDPOS = "full_moon";
    public static final String UNSELECTEDPOS = "=";

    // The default color for embeds
    public static final Color EMB_COL = new Color(100, 243, 213);
    public static final int PIN_THRESHOLD = 1;
    public static long timeStarted = 0;
    public static final Long INITIAL_MEMORY =
            Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

    // The limit of a playlist that can be created within the bot
    public static final int CUSTOM_PLAYLIST_SIZE_LIMIT = 150;

    // How often to update the message when loading or creating a new playlist through the bot
    public static final int PLAYLIST_UPDATE_INTERVAL = 10;

    private static Constants instance;

    private Constants() {}

    public static Constants getInstance() {
        if (instance == null) {
            instance = new Constants();
        }
        return instance;
    }

    public static void init() {
        timeStarted = System.currentTimeMillis();
        getInstance();
    }
}
