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

    public void addCredit(int amt) {
        long credit = getCredit();
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
     * The formula for the level to exp calculation
     * @return An integer representing how much experience the user will need for their next level
     */
    public int getExpForNextLevel() {
        int currentLevel = (int) getLevel();
        return (10 * (currentLevel+1) * (currentLevel+2) - (8*currentLevel));
    }

    public int getPercentageExp() {
        int exp = (int) getExp();
        int expToProgress = getExpForNextLevel();
        return (int) Math.round((double) exp/expToProgress*100);
    }

    private void setLevel(int lvl) {
        userFileManager.updateValue("level", (long) lvl);
    }

    private void setExp(int exp) {
        userFileManager.updateValue("score", (long) exp);
    }

    public long getCredit() {
        return (Long) userFileManager.getValue("credit");
    }

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
