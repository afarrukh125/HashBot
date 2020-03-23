package me.afarrukh.hashbot.data;

import net.dv8tion.jda.api.entities.Member;

/**
 * Created by Abdullah on 01/05/2019 04:09
 * <p>
 * Used as a temporary class to map a member to their level and experience value
 */
class MemberData {
    private final Member member;
    private final int level;
    private final long exp;

    public MemberData(Member member, int level, long exp) {
        this.member = member;
        this.level = level;
        this.exp = exp;
    }

    public Member getMember() {
        return member;
    }

    public int getLevel() {
        return level;
    }

    public long getExp() {
        return exp;
    }
}
