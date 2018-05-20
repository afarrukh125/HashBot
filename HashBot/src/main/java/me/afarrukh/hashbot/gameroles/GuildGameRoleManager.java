package me.afarrukh.hashbot.gameroles;

import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.JSONGuildManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class GuildGameRoleManager {

    private ArrayList<GameRole> gameRoles;
    private ArrayList<RoleBuilder> roleBuilders;

    private Guild guild;

    public GuildGameRoleManager(Guild guild) {
        this.guild = guild;
        gameRoles = new ArrayList<>();
        roleBuilders = new ArrayList<>();
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

    public ArrayList<GameRole> getGameRoles() {
        return gameRoles;
    }

    public ArrayList<RoleBuilder> getRoleBuilders() {
        return roleBuilders;
    }
}
