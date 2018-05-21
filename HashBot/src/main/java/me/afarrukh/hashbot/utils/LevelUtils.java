package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.entities.Invoker;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class LevelUtils {

    /**
     * Given the message that the user has entered, gives them experience and a random value between 1 and 5 bonus
     * @param tokens
     * @return The experience to add to the user given their message
     */
    public static int getPointsFromMessage(String[] tokens, int level) {
        int sum = 0;
        Random random = new Random();
        int rng = random.nextInt(level) + 1;
        sum = Constants.INITIAL_EXP + tokens.length + rng + level;

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
        StringBuilder val = new StringBuilder();
        for(int i = 0; i<10; i++) {
            if(i < num)

                val.append(":" + Constants.LEFTBAR + ":");
            else
                val.append(":" + Constants.RIGHTBAR + ":");
        }
        return val.toString();
    }

    /**
     * Sorts the members of a guild in order of their level and experience points (score)
     * Uses TimSort
     * @param g The guild to get the member list from
     * @return An array of type User which returns a sorted array of User objects
     */
    public static Member[] getLeaderboard(Guild g) {

        ArrayList<Member> memberList = new ArrayList<>();
        for(Member m: g.getMembers()) {
            if(!m.getUser().isBot())
                memberList.add(m);
        }

        Comparator<Member> memberSorter = new Comparator<Member>() {

            public int compare(Member m1, Member m2) {
                Invoker in1 = new Invoker(m1);
                Invoker in2 = new Invoker(m2);
                if(in2.getLevel() > in1.getLevel())
                    return 1;
                if(in1.getLevel() == in2.getLevel()) {
                    if(in2.getExp() > in1.getExp())
                        return 1;
                    return -1;
                }
                return -1;
            }
        };

        memberList.sort(memberSorter);

        Member[] userArray = new Member[memberList.size()];

        return memberList.toArray(userArray);
    }
}
