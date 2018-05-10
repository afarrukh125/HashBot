package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.config.Constants;

import java.util.Random;

public class LevelUtils {

    /**
     * Given the message that the user has entered, gives them experience and a random value between 1 and 5 bonus
     * @param tokens
     * @return The experience to add to the user given their message
     */
    public static int getPointsFromMessage(String[] tokens) {
        int sum = 0;
        Random random = new Random();
        int rng = random.nextInt(5) + 1;
        sum = Constants.INITIAL_EXP + tokens.length + rng;

        if(sum > Constants.MAX_EXP_FROM_MSG)
            return Constants.MAX_EXP_FROM_MSG;

        return sum;
    }

    /**
     * Gets a string with 10 characters used mainly to represent the experience progress of a user
     * @param num The number up to which the bar will appear "filled"
     * @return A string for this bar
     */
    public static String getBar(int num) {
        String val = "";
        for(int i = 0; i<10; i++) {
            if(i < num)

                val += ":"+Constants.LEFTBAR+":";
            else
                val += ":"+Constants.RIGHTBAR+":";
        }
        return val;
    }
}
