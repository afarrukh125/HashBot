package me.afarrukh.hashbot.entities;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.DataManager;
import me.afarrukh.hashbot.data.UserDataManager;
import me.afarrukh.hashbot.gameroles.GameRole;
import me.afarrukh.hashbot.utils.BotUtils;
import me.afarrukh.hashbot.utils.LevelUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.ArrayList;
import java.util.Random;

/**
 * Views the invoker as a member who has initiated commands from this bot that would lead to a change in their user
 * properties (e.g. colour change, name change)
 * */

public class Invoker {

    private final Member member;
    private final DataManager userFileManager;

    public Invoker(Member m) {
        member = m;
        userFileManager = new UserDataManager(m);
    }

    public Role getRole(String name) {
        for(Role r: member.getRoles()) {
            if(r.getName().equalsIgnoreCase(name))
                return r;
        }
        return null;
    }

    /**
     * Checks if the current UNIX time stamp exceeds 60000 (a minute) of that in the user file of the associated User object.
     * <br/>If enough time has passed then the timestamp is set to the current time.
     * @return true if enough time has passed, false otherwise
     */
    public boolean hasTimePassed() {
        long time = (Long) userFileManager.getValue("time");
        if((System.currentTimeMillis() - time) < Constants.minToMillis)
            return false;

        userFileManager.updateValue("time", System.currentTimeMillis());
        return true;
    }

    public void addCredit(long amt) {
        long credit = getCredit();
        if(credit >= Long.MAX_VALUE) {
            userFileManager.updateValue("credit", Math.abs(Long.MAX_VALUE));
            return;
        }
        credit += amt;
        userFileManager.updateValue("credit", credit);
    }

    /**
     * Adds a random amount of credit to a user
     */
    public void addRandomCredit() {
        addCredit(new Random().nextInt(Constants.MAX_CREDIT) + 1);
    }

    public void updateExperience(String msg) {
        String[] tokens = msg.split(" ");
        int amt = LevelUtils.getPointsFromMessage(tokens, (int) getLevel());
        int currentExp = (int) getExp();

        this.setExp(currentExp + amt);
        int newExp = currentExp + amt;

        //System.out.println("User " +member.getUser().getName()+ " now has " +(newExp)+ " experience. (Added " +amt+ ")");

        int expForNextLevel = getExpForNextLevel();

        if(newExp >= expForNextLevel) {
            int currentLevel = (int) getLevel();

            setExp(newExp - expForNextLevel);
            setLevel(currentLevel+1);

            System.out.println("<"
                    + member.getGuild().getName()+ "> " +
                    member.getUser().getName() + " has levelled up. (Now level " +(currentLevel+1)+")");
        }
    }

    /**

     * @return An integer representing how much experience the user will need for their next level
     */
    public int getExpForNextLevel() {
        int currentLevel = (int) getLevel();
        return getExperienceForNextLevel(currentLevel);
    }

    /**
     * * The formula for the level to exp calculation
     * @param level
     * @return
     */
    public static int getExperienceForNextLevel(int level) {
        return 10 * (level+1) * (level+2);
    }

    /**
     * Returns the level given from the exp and the remaining exp spare.
     * @param exp The experience to calculate from.
     * @return An integer array where the value at index 0 is the level and index 1 is the spare experience.
     */
    public static int[] parseLevelFromTotalExperience(int exp) {
        int level = 1;
        int[] data = new int[2];
        while(exp > getExperienceForNextLevel(level)) {
            exp -= getExperienceForNextLevel(level);
            level += 1;
        }
        data[0] = level;
        data[1] = exp;
        return data;
    }

    /**
     * Gets the total experience for a given level
     * @param lvl The level to calculate from
     * @return The total experience for the given level
     */
    public static int parseTotalExperienceFromLevel(int lvl) {
        int exp = 0;
        for(int i = 1; i<lvl; i++) {
            exp += getExperienceForNextLevel(i);
        }
        return exp;
    }

    public int getPercentageExp() {
        int exp = (int) getExp();
        int expToProgress = getExpForNextLevel();
        return (int) Math.round((double) exp/expToProgress*100);
    }

    public static int getPercentageExp(int exp, int level) {
        int expToProgress = getExperienceForNextLevel(level);
        return (int) Math.round((double) exp/expToProgress*100);
    }

    private void setLevel(int lvl) {
        userFileManager.updateValue("level", (long) lvl);
    }

    private void setExp(int exp) {
        userFileManager.updateValue("score", (long) exp);
    }

    public long getCredit() {
        long value = Math.abs((Long) userFileManager.getValue("credit"));
        if(value <= -Long.MAX_VALUE) {
            userFileManager.updateValue("credit", Long.MAX_VALUE - 1);
            return Long.MAX_VALUE-1;
        }
        return value;
    }

//    public static void main(String[] args) {
//        int x = parseTotalExperienceFromLevel(34) + 4801;
//        int y = parseTotalExperienceFromLevel(23) + 663;
//        System.out.println(x);
//        System.out.println(y);
//        System.out.println(x + y);
//        System.out.println(parseLevelFromTotalExperience(x+y)[0] + ", " + parseLevelFromTotalExperience(x+y)[1]);
//    }

    public long getLevel() {
        return (Long) userFileManager.getValue("level");
    }

    public long getExp() {
        return (Long) userFileManager.getValue("score");
    }

    public ArrayList<Role> getGameRolesAsRoles() {
        ArrayList<Role> roleList = new ArrayList<>();

        for(Role r: member.getRoles()) {
            if(BotUtils.isGameRole(r, member.getGuild()))
                roleList.add(r);
        }

        return roleList;
    }

    public ArrayList<GameRole> getGameRoles() {
        ArrayList<GameRole> roleList = new ArrayList<>();

        for(GameRole gr: Bot.gameRoleManager.getGuildRoleManager(member.getGuild()).getGameRoles()) {
            for(Role r: member.getRoles()) {
                if(r.getName().equalsIgnoreCase(gr.getName()))
                    roleList.add(gr);
            }
        }

        return roleList;
    }

    public Member getMember() {
        return member;
    }

}
