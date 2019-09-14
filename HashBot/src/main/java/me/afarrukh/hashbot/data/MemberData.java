package me.afarrukh.hashbot.data;

import net.dv8tion.jda.core.entities.Member;

/**
 * Created by Abdullah on 01/05/2019 04:09
 * <p>
 * Used as a temporary class to map a member to their level and experience value
 */
class MemberData {
    private Member member;
    private int level;
    private long exp;

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
