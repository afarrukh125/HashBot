package me.afarrukh.hashbot.entities;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.IDataManager;
import me.afarrukh.hashbot.data.SQLUserDataManager;
import me.afarrukh.hashbot.gameroles.GameRole;
import me.afarrukh.hashbot.utils.BotUtils;
import me.afarrukh.hashbot.utils.LevelUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.*;

/**
 * Views the invoker as a member who has initiated commands from this bot that would lead to a change in their user
 * properties (e.g. colour change, name change)
 */

public class Invoker {

    private static final Map<Member, Invoker> memberInvokerMap = new HashMap<>();
    private final Member member;
    private final IDataManager userFileManager;
    private long credit;

    private Invoker(Member m) {
        member = m;
        userFileManager = new SQLUserDataManager(m);
        credit = Long.parseLong((String) userFileManager.getValue("credit"));
    }

    public static Invoker of(Member m) {
        Invoker retrievedMember = memberInvokerMap.get(m);
        if (retrievedMember == null) {
            retrievedMember = new Invoker(m);
            memberInvokerMap.put(m, retrievedMember);
        }
        return retrievedMember;
    }

    /**
     * * The formula for the level to exp calculation
     *
     * @param level The level to calculate experience for
     * @return An integer corresponding to how much is required for the next level given the current level
     */
    public static int getExperienceForNextLevel(int level) {
        return 10 * (level + 1) * (level + 2);
    }

    public record ExperienceData(int level, long exp){}

    /**
     * Returns the level given from the exp and the remaining exp spare.
     *
     * @param exp The experience to calculate from.
     * @return An integer array where the value at index 0 is the level and index 1 is the spare experience.
     */
    public static ExperienceData parseLevelFromTotalExperience(long exp) {
        int level = 1;
        while (exp > getExperienceForNextLevel(level)) {
            exp -= getExperienceForNextLevel(level);
            level++;
        }
        return new ExperienceData(level, exp);
    }

    /**
     * Gets the total experience for a given level
     *
     * @param lvl The level to calculate from
     * @return The total experience for the given level
     */
    public static int parseTotalExperienceFromLevel(int lvl) {
        int exp = 0;
        for (int i = 1; i < lvl; i++) {
            exp += getExperienceForNextLevel(i);
        }
        return exp;
    }

    public static int getPercentageExp(long exp, int level) {
        int expToProgress = getExperienceForNextLevel(level);
        return (int) Math.round((double) exp / expToProgress * 100);
    }

    public Role getRole(String name) {
        for (Role r : member.getRoles()) {
            if (r.getName().equalsIgnoreCase(name))
                return r;
        }
        return null;
    }

    /**
     * Checks if the current UNIX time stamp exceeds 60000 (a minute) of that in the user file of the associated User object.
     * <br/>If enough time has passed then the timestamp is set to the current time.
     *
     * @return true if enough time has passed, false otherwise
     */
    public boolean hasTimePassed() {
        long time = Long.parseLong((String) userFileManager.getValue("time"));
        if ((System.currentTimeMillis() - time) < (Constants.minToMillis *0.75))
            return false;

        userFileManager.updateValue("time", System.currentTimeMillis());
        return true;
    }

    public void addCredit(long amt) {
        if (this.credit >= Integer.MAX_VALUE) {
            userFileManager.updateValue("credit", Integer.MAX_VALUE);
            credit = Integer.MAX_VALUE;
            return;
        }
        userFileManager.updateValue("credit", credit + amt);
        credit += amt;
    }

    /**
     * Adds a random amount of credit to a user
     */
    public void addRandomCredit() {
        addCredit(new Random().nextInt(Constants.MAX_CREDIT) + 1);
    }

    public void updateExperience(String msg) {
        String[] tokens = msg.split(" ");
        int amt = LevelUtils.getPointsFromMessage(tokens, getLevel());
        int currentExp = (int) getExp();

        this.setExp(currentExp + amt);

        checkExperience(currentExp, amt);
    }

    /**
     * @return An integer representing how much experience the user will need for their next level
     */
    public int getExpForNextLevel() {
        int currentLevel = getLevel();
        return getExperienceForNextLevel(currentLevel);
    }

    public int getPercentageExp() {
        int exp = (int) getExp();
        int expToProgress = getExpForNextLevel();
        return (int) Math.round((double) exp / expToProgress * 100);
    }

    public long getCredit() {
        return credit;
    }

    public int getLevel() {
        return (Integer) userFileManager.getValue("level");
    }

    private void setLevel(int lvl) {
        userFileManager.updateValue("level", (long) lvl);
    }

    public long getExp() {
        try {
            return Long.parseLong((String) userFileManager.getValue("exp"));
        } catch (NumberFormatException e) {
            return Long.parseLong(((String) userFileManager.getValue("exp")).split("\\.")[0]);
        }
    }

    private void setExp(int exp) {
        userFileManager.updateValue("exp", (long) exp);
    }

    public ArrayList<Role> getGameRolesAsRoles() {
        ArrayList<Role> roleList = new ArrayList<>();

        for (Role r : member.getRoles()) {
            if (BotUtils.isGameRole(r, member.getGuild()))
                roleList.add(r);
        }

        return roleList;
    }

    public ArrayList<GameRole> getGameRoles() {
        ArrayList<GameRole> roleList = new ArrayList<>();

        for (GameRole gr : Bot.gameRoleManager.getGuildRoleManager(member.getGuild()).getGameRoles()) {
            for (Role r : member.getRoles()) {
                if (r.getName().equalsIgnoreCase(gr.getName()))
                    roleList.add(gr);
            }
        }

        return roleList;
    }

    private void addExperience(int amount) {
        setExp((int) getExp() + amount);
    }

    public void addRandomExperience() {
        int currentLevel = getLevel();

        // Add bonus to help cope with higher level requirements.
        int bonus = (int) Math.floor((float) currentLevel/10) * currentLevel;

        int amt = new Random().nextInt(Constants.RANDOM_EXPERIENCE_BOUND) + bonus;

        int currentExp = (int) getExp();

        addExperience(amt);

        checkExperience(currentExp, amt);
    }

    private void checkExperience(int currentExp, int amt) {
        int newExp = currentExp + amt;

        int expForNextLevel = getExpForNextLevel();

        if (newExp >= expForNextLevel) {
            int currentLevel = getLevel();

            setExp(newExp - expForNextLevel);
            setLevel(currentLevel + 1);

            System.out.println(new Date(System.currentTimeMillis()) + ": <"
                    + member.getGuild().getName() + "> " +
                    member.getUser().getName() + " has levelled up. (Now level " + (currentLevel + 1) + ")");
        }
    }

    public Member getMember() {
        return member;
    }

}
