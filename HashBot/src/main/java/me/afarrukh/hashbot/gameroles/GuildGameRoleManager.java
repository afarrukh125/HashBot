package me.afarrukh.hashbot.gameroles;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.DataManager;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.utils.BotUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Further information regarding GameRoles is given in the GameRoleManager class.
 * This class represents a GameRoleManager for an individual guild
 */
public class GuildGameRoleManager {

    private final ArrayList<GameRole> gameRoles;

    private final Map<GameRole, Role>  roleMap;
    private final ArrayList<RoleBuilder> roleBuilders;
    private final ArrayList<RoleAdder> roleAdders;
    private final ArrayList<RoleRemover> roleRemovers;
    private final ArrayList<RoleDeleter> roleDeleters;

    private String prefix = Constants.invokerChar;

    private final Guild guild;

    public GuildGameRoleManager(Guild guild) {
        this.guild = guild;
        gameRoles = new ArrayList<>();
        roleBuilders = new ArrayList<>();
        roleAdders = new ArrayList<>();
        roleRemovers = new ArrayList<>();
        roleDeleters = new ArrayList<>();
        init();

        roleMap = new HashMap<>();
    }

    private void init() {
        DataManager jgm = new GuildDataManager(guild);
        JSONArray arr = (JSONArray) jgm.getValue("gameroles");
        //noinspection unchecked
        Iterator<Object> iter = arr.iterator();
        while(iter.hasNext()) {
            JSONObject roleObj = (JSONObject) iter.next();
            String roleName = (String) roleObj.get("name");
            if(BotUtils.doesRoleExist(guild, roleName))
                gameRoles.add(new GameRole(roleName, (String)roleObj.get("creatorId")));
            else
                iter.remove();
        }
        jgm.updateValue("gameroles", arr);
        String prefix = (String) jgm.getValue("prefix");
        if(prefix != null)
            this.prefix = prefix;
        else
            jgm.updateValue("prefix", Constants.invokerChar);
    }

    public RoleBuilder builderForUser(User user) {
        for(RoleBuilder builder : roleBuilders) {
            if (builder.getUser() == user) return builder;
        }
        return null;
    }

    public RoleAdder adderForUser(User user) {
        for(RoleAdder adder : roleAdders) {
            if (adder.getUser() == user) return adder;
        }
        return null;
    }

    public RoleRemover removerForUser(User user) {
        for(RoleRemover remover: roleRemovers)
            if(remover.getUser() == user) return remover;
        return null;
    }

    public RoleDeleter deleterForUser(User user) {
        for(RoleDeleter deleter: roleDeleters)
            if(deleter.getUser() == user) return deleter;
        return null;
    }

    public void remove(String name) {
        gameRoles.removeIf(gameRole -> gameRole.getName().equalsIgnoreCase(name));
    }

    public void setPrefix(String prefix) {
        GuildDataManager jgm = new GuildDataManager(guild);
        jgm.updateValue("prefix", prefix);
        this.prefix = prefix;
    }

    public GameRole getGameRoleFromRole(Role r) {
        for(GameRole gr: gameRoles) {
            if(gr.getName().equalsIgnoreCase(r.getName()))
                    return gr;
        }
        return null;
    }

    public Role getRoleFromGameRole(GameRole gr) {
        if(gr == null)
            return null;

        Role role = roleMap.get(gr);

        if(role != null)
            return role;

        for(Role r: guild.getRoles()) {
            if(r.getName().equalsIgnoreCase(gr.getName())) {
                roleMap.put(gr, r);
                return r;
            }
        }
        return null;
    }

    public ArrayList<RoleAdder> getRoleAdders() {
        return roleAdders;
    }

    public ArrayList<GameRole> getGameRoles() {
        return gameRoles;
    }

    public List<Role> getGameRolesAsRoles() {
        return new ArrayList<>(roleMap.values());
    }

    public ArrayList<RoleBuilder> getRoleBuilders() {
        return roleBuilders;
    }

    public ArrayList<RoleRemover> getRoleRemovers() {
        return roleRemovers;
    }

    public ArrayList<RoleDeleter> getRoleDeleters() {
        return roleDeleters;
    }

    public String getPrefix() {
        return prefix;
    }
}
