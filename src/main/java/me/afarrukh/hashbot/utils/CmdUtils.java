package me.afarrukh.hashbot.utils;

import java.util.concurrent.TimeUnit;

public class CmdUtils {

    /**
     * The given string starting from the given start index and ending at a given index
     *
     * @param tokens     - the string array to be evaluated
     * @param startIndex - the start index
     * @param endIndex   - the end index
     * @return - a string with all array elements given as a single string
     */
    public static String getParamsAsString(String[] tokens, int startIndex, int endIndex) {
        StringBuilder params = new StringBuilder();
        for (int i = startIndex; i <= endIndex; i++) params.append(tokens[i]).append(" ");

        return params.toString().trim();
    }

    public static String longToHHMMSS(long count) {
        return String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(count),
                TimeUnit.MILLISECONDS.toMinutes(count) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(count) % TimeUnit.MINUTES.toSeconds(1));
    }

    public static String longToMMSS(long count) {
        long time = count / 1000;
        return String.format("%02d:%02d", time / 60, time % 60);
    }
}
