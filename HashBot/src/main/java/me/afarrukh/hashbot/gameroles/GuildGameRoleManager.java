package me.afarrukh.hashbot.gameroles;

import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.JSONGuildManager;
import me.afarrukh.hashbot.utils.BotUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class GuildGameRoleManager {

    private final ArrayList<GameRole> gameRoles;
    private final ArrayList<RoleBuilder> roleBuilders;
    private final ArrayList<RoleAdder> roleAdders;
    private final ArrayList<RoleRemover> roleRemovers;

    private final Guild guild;

    public GuildGameRoleManager(Guild guild) {
        this.guild = guild;
        gameRoles = new ArrayList<>();
        roleBuilders = new ArrayList<>();
        roleAdders = new ArrayList<>();
        roleRemovers = new ArrayList<>();
        init();
    }

    private void init() {
        JSONGuildManager jgm = new JSONGuildManager(guild);
        JSONArray arr = (JSONArray) jgm.getValue("gameroles");
        Iterator<Object> iter = arr.iterator();
        while(iter.hasNext()) {
            JSONObject roleObj = (JSONObject) iter.next();
            String roleName = (String) roleObj.get("name");
            if(BotUtils.doesRoleExist(guild, roleName))
                gameRoles.add(new GameRole(roleName, (String)roleObj.get("creatorId")));
            else
                iter.remove();
        }
        jgm.updateField("gameroles", arr);
    }

    public RoleBuilder builderForUser(User user) {
        for(RoleBuilder builder : Bot.gameRoleManager.getGuildRoleManager(guild).roleBuilders) {
            if (builder.getUser() == user) return builder;
        }
        return null;
    }

    public RoleAdder adderForUser(User user) {
        for(RoleAdder adder : Bot.gameRoleManager.getGuildRoleManager(guild).roleAdders) {
            if (adder.getUser() == user) return adder;
        }
        return null;
    }

    public RoleRemover removerForUser(User user) {
        for(RoleRemover remover: Bot.gameRoleManager.getGuildRoleManager(guild).roleRemovers) {
            if(remover.getUser() == user) return remover;
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

    public GameRole getGameRoleFromRole(Role r) {
        for(GameRole gr: gameRoles) {
            if(gr.getName().equalsIgnoreCase(r.getName()))
                    return gr;
        }
        return null;
    }

    public Role getRoleFromGameRole(GameRole gr) {
        for(Role r: guild.getRoles()) {
            if(r.getName().equalsIgnoreCase(gr.getName()))
                return r;
        }
        return null;
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

    public ArrayList<RoleRemover> getRoleRemovers() {
        return roleRemovers;
    }
}
