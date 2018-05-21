package me.afarrukh.hashbot.utils;

import java.util.concurrent.TimeUnit;

public class CmdUtils {

    /**
     * Gets the given array of strings as a string starting from index 1
     * @param tokens - the array to be evaluated
     * @return - a string with all array elements given as a single string
     */
    public static String getParamsAsString(String[] tokens) {
        StringBuilder params = new StringBuilder();
        for(int i=1; i<tokens.length; i++) {
            params.append(tokens[i]).append(" ");
        }
        return params.toString().trim();
    }

    /**
     * Gets the given string starting from the given start index
     * @param tokens - the string array to be evaluated
     * @param startIndex - the start index
     * @return - a string with all array elements given as a single string
     */
    public static String getParamsAsString(String[] tokens, int startIndex) {
        StringBuilder params = new StringBuilder();
        for(int i=startIndex; i<tokens.length; i++) {
            params.append(tokens[i]).append(" ");
        }
        return params.toString().trim();
    }

    /**
     * The given string starting from the given start index and ending at a given index
     * @param tokens - the string array to be evaluated
     * @param startIndex - the start index
     * @param endIndex - the end index
     * @return - a string with all array elements given as a single string
     */
    public static String getParamsAsString(String[] tokens, int startIndex, int endIndex) {
        StringBuilder params = new StringBuilder();
        for(int i=startIndex; i<=endIndex; i++) {
            params.append(tokens[i]).append(" ");
        }
        return params.toString().trim();
    }

    /**
     * Takes a given array and prints each element line by line
     * @param arr - the array to be printed
     */
    public static void printArray(String[] arr) {
        for(int i = 0; i<arr.length; i++)
            System.out.println(arr[i]);
    }

    /**
     * Converts long to HHMMSS
     * @param count The long to be converted
     * @return A String in HHMMSS format
     */
    public static String longToHHMMSS(long count) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(count),
                TimeUnit.MILLISECONDS.toMinutes(count) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(count) % TimeUnit.MINUTES.toSeconds(1));
    }

    /**
     * Converts a long to MMSS
     * @param count The long to be converted
     * @return A String in MMSS format
     */
    public static String longToMMSS(long count) {
        long time = count/1000;
        return String.format("%02d:%02d", time/60, time%60);
    }

}
