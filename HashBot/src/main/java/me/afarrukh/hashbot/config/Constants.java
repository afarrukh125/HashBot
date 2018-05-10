package me.afarrukh.hashbot.config;

import java.awt.*;

public class Constants {

    public static String invokerChar = "!";

    public static final String LiveLife[] = {"279696640645005322", //INDEX 0 IS DEFAULT CHANNEL ID
            "440990144900759552"}; //INDEX 1 IS BOT SPAM CHANNEL

    public static final String BotChan[] = {"281033379150036992",
            "429234962047172639"};

    public static final long dayToMillis = 86400000;
    public static final long minToMillis = 60000;

    public static final int colChangeCred = 1500;

    public static final int PRUNE_COST = 30;

    public static final String LEFTBAR = "full_moon"; //Emoji name for experience bar in stats command
    public static final String RIGHTBAR = "new_moon";

    public static final String OWNER_ID = "111608457290895360";

    public static final int MAX_CREDIT = 30;

    public static final int MAX_EXP_FROM_MSG = 100; //A single message cannot give more than this much experience
    public static final int LEADERBOARD_MAX = 10;
    public static final int INITIAL_EXP = 15; //The amount of experience the user gets per message
    public static final int IMAGE_EXP = INITIAL_EXP*2; //The amount of experience the user gets when they send an image (flat amount)

    public static final int SONG_COST = 15;
    public static final int PLAY_TOP_COST = 30;
    public static final int MAX_PLAYLIST_SIZE = 300;

    public static final int MUSICBAR_SCALE = 35;
    public static final int MAX_VOL = 100; //The maximum volume the bot can play at.
    public static final int DEFAULT_VOL = 95;

    public static final int ITERABLE_MESSAGES = 500;

    public static final String SELECTEDPOS = "full_moon";
    public static final String UNSELECTEDPOS = "=";

    public static final Color EMB_COL = new Color(100, 243, 213); //The default color for embeds

}
