package me.afarrukh.hashbot.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LevelUtils {

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

        List<Member> memberList = g.getMembers().stream()
                .filter(m -> !m.getUser().isBot())
                .filter(m ->
                        !m.getUser().getId().equals(g.getJDA().getSelfUser().getId()))
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
