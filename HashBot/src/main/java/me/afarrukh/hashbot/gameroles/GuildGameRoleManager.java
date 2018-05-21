package me.afarrukh.hashbot.gameroles;

import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.JSONGuildManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class GuildGameRoleManager {

    private ArrayList<GameRole> gameRoles;
    private ArrayList<RoleBuilder> roleBuilders;
    private ArrayList<RoleAdder> roleAdders;

    private Guild guild;

    public GuildGameRoleManager(Guild guild) {
        this.guild = guild;
        gameRoles = new ArrayList<>();
        roleBuilders = new ArrayList<>();
        roleAdders = new ArrayList<>();
        init();
    }

    private void init() {
        JSONGuildManager jgm = new JSONGuildManager(guild);
        JSONArray arr = (JSONArray) jgm.getValue("gameroles");
        for(Object obj: arr) {
            JSONObject roleObj = (JSONObject) obj;
            gameRoles.add(new GameRole((String)roleObj.get("name"), (long)roleObj.get("red"), (long)roleObj.get("green"), (long)roleObj.get("blue")));
        }
    }

    public RoleBuilder builderForUser(User user) {
        for(RoleBuilder builder : Bot.gameRoleManager.getGuildRoleManager(guild).roleBuilders) {
            if (builder.user == user) return builder;
        }
        return null;
    }

    public RoleAdder adderForUser(User user) {
        for(RoleAdder adder : Bot.gameRoleManager.getGuildRoleManager(guild).roleAdders) {
            if (adder.user == user) return adder;
        }
        return null;
    }

    public void remove(String name) {
        Iterator<GameRole> iter = gameRoles.iterator();
        while(iter.hasNext()) {
            GameRole gameRole = iter.next();
            if(gameRole.getName().equalsIgnoreCase(name))
                iter.remove();
        }
    }

    public ArrayList<RoleAdder> getRoleAdders() {
        return roleAdders;
    }

    public ArrayList<GameRole> getGameRoles() {
        return gameRoles;
    }

    public ArrayList<RoleBuilder> getRoleBuilders() {
        return roleBuilders;
    }
}
