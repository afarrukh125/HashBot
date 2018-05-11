package me.afarrukh.hashbot.entities;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.JSONUserFileManager;
import me.afarrukh.hashbot.utils.LevelUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.Random;

/**
 * Views the invoker as a member who has initiated commands from this bot that would lead to a change in their user
 * properties (e.g. colour change, name change)
 * */

public class Invoker {

    private Member member;
    private JSONUserFileManager jsonFileManager;

    public Invoker(Member m) {
        member = m;
        jsonFileManager = new JSONUserFileManager(m);
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
        long time = (Long) jsonFileManager.getValue("time");
        if((System.currentTimeMillis() - time) < Constants.minToMillis)
            return false;

        jsonFileManager.updateField("time", System.currentTimeMillis());
        return true;
    }

    public void addCredit(int amt) {
        long credit = getCredit();
        credit += amt;
        jsonFileManager.updateField("credit", credit);
        System.out.print("User "+member.getUser().getName()+ " now has " + credit+ " credit. (Added " +amt+ ") | ");
    }

    /**
     * Adds a random amount of credit to a user
     */
    public void addRandomCredit() {
        Random random = new Random();
        int rng = random.nextInt(Constants.MAX_CREDIT) + 1;

        addCredit(rng);
    }

    public void updateExperience(String msg) {
        String[] tokens = msg.split(" ");
        int amt = LevelUtils.getPointsFromMessage(tokens);
        int currentExp = (int) getExp();
        setExp(currentExp + amt);
        System.out.print("User " +member.getUser().getName()+ " now has " +(currentExp+amt)+ " experience. (Added " +amt+ ")\n");

        if(currentExp >= getExpForNextLevel()) {
            int currentLevel = (int) getLevel();
            int newExp = (int) getExp();

            setExp(newExp - getExpForNextLevel());
            setLevel(currentLevel+1);
            System.out.println(member.getUser().getName() + " has levelled up. (Now level " +(currentLevel+1)+")");
        }
    }

    /**
     * The formula for the level to exp calculation
     * @return An integer representing how much experience the user will need for their next level
     */
    public int getExpForNextLevel() {
        int currentLevel = (int) getLevel();
        return 10 * (currentLevel+1) * (currentLevel+2);
    }

    public void setLevel(int lvl) {
        jsonFileManager.updateField("level", (long) lvl);
    }

    public void setExp(int exp) {
        jsonFileManager.updateField("score", (long) exp);
    }

    public long getCredit() {
        return (Long) jsonFileManager.getValue("credit");
    }

    public long getLevel() {
        return (Long) jsonFileManager.getValue("level");
    }

    public long getExp() {
        return (Long) jsonFileManager.getValue("score");
    }

    public Member getMember() {
        return member;
    }

}
