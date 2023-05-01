package me.afarrukh.hashbot.utils;

public class ExperienceUtils {
    public static int getExperienceForNextLevel(int level) {
        return 10 * (level + 1) * (level + 2);
    }

    public static long parseTotalExperienceFromLevel(int level) {
        int exp = 0;
        for (int i = 1; i < level; i++) {
            exp += getExperienceForNextLevel(i);
        }
        return exp;
    }

    public static int getPercentageExperience(long exp, int level) {
        int expToProgress = getExperienceForNextLevel(level);
        return (int) Math.round((double) exp / expToProgress * 100);
    }
}
