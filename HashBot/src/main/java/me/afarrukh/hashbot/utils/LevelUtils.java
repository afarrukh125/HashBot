package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.SQLUserDataManager;
import me.afarrukh.hashbot.entities.Invoker;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LevelUtils {

    /**
     * Given the message that the user has entered, gives them experience and a random value between 1 and 5 bonus
     *
     * @param tokens The message provided for which experience is to be calculated
     * @return The experience to add to the user given their message
     */
    public static int getPointsFromMessage(String[] tokens, int level) {
        Random random = new Random();
        int rng = random.nextInt(level) + 1;
        int sum = Constants.BASE_EXP + tokens.length + rng + level;

        return Math.min(sum, Constants.MAX_EXP_FROM_MSG);
    }

    /**
     * Gets a string with 10 characters used mainly to represent the experience progress of a user
     *
     * @param num The number up to which the bar will appear "filled"
     * @return A string for this bar
     */
    public static String getBar(int num) {
        StringBuilder val = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i < num)

                val.append(":" + Constants.LEFTBAR + ":");
            else
                val.append(":" + Constants.RIGHTBAR + ":");
        }
        return val.toString();
    }

    /**
     * Sorts the members of a guild in order of their level and experience points (score)
     * Uses TimSort
     *
     * @param g The guild to get the member list from
     * @return An array of type User which returns a sorted array of User objects
     */
    public static List<Member> getLeaderboard(Guild g) {

        return SQLUserDataManager.getMemberData(g);
    }

    public static List<Member> getCreditsLeaderboard(Guild g) {

        List<Member> memberList = g.getMembers()
                .stream()
                .filter(m -> !m.getUser().isBot())
                .filter(m -> !m.getUser().getId().equals(g.getJDA().getSelfUser().getId()))
                .collect(Collectors.toCollection((Supplier<List<Member>>) ArrayList::new));

        Comparator<Member> memberSorter = (m1, m2) -> {
            Invoker in1 = Invoker.of(m1);
            Invoker in2 = Invoker.of(m2);
            return Math.toIntExact(in2.getCredit() - in1.getCredit());
        };

        memberList.sort(memberSorter);

        return memberList;
    }
}
