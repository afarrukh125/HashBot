package me.afarrukh.hashbot.utils;

import java.util.concurrent.TimeUnit;

public class CmdUtils {

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
