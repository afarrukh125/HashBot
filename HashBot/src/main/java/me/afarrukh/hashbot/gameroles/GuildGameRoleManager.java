package me.afarrukh.hashbot.gameroles;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.DataManager;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Further information regarding GameRoles is given in the GameRoleManager class.
 * This class represents a GameRoleManager for an individual guild
 */
public class GuildGameRoleManager {

    private final Map<GameRole, Role> roleMap;
    private final Map<User, RoleGUI> roleModifiers; // Object pool design pattern to ensure 1-1 constraint
    private final Guild guild;
    private String prefix = Constants.invokerChar;
    private int pinThreshold = Constants.PIN_THRESHOLD;

    public GuildGameRoleManager(Guild guild) {
        this.guild = guild;
        roleModifiers = new HashMap<>();

        roleMap = new HashMap<>();
        init();
    }

    private void init() {
        DataManager guildDataManager = GuildDataMapper.getInstance().getDataManager(guild);

        loadRolesFromDatabase(guildDataManager);

        setupCommandPrefix(guildDataManager);

        setupPinnedMessageThreshold(guildDataManager);

    }

    private void loadRolesFromDatabase(DataManager guildDataManager) {

        JSONArray arr = (JSONArray) guildDataManager.getValue(Key.GAMEROLES.string());
        //noinspection unchecked
        Iterator<Object> iter = arr.iterator();
        while (iter.hasNext()) {
            JSONObject roleObj = (JSONObject) iter.next();
            String roleName = (String) roleObj.get(Key.NAME.string());
            List<Role> roleList = guild.getRolesByName(roleName, true);
            if (!roleList.isEmpty()) {
                Role role = roleList.get(0);
                GameRole gameRole = new GameRole(roleName, (String) roleObj.get(Key.CREATOR.string()));
                roleMap.put(gameRole, role);
            } else
                iter.remove();
        }
        guildDataManager.updateValue(Key.GAMEROLES.string(), arr);
    }

    private void setupCommandPrefix(DataManager guildDataManager) {
        String prefix = (String) guildDataManager.getValue(Key.PREFIX.string());

        if (prefix != null)
            this.prefix = prefix;
        else
            guildDataManager.updateValue(Key.PREFIX.string(), Constants.invokerChar);
    }

    private void setupPinnedMessageThreshold(DataManager guildDataManager) {
        String threshold = (String) guildDataManager.getValue(Key.PINNED_THRESHOLD.string());
        if (threshold != null)
            this.pinThreshold = Integer.parseInt(threshold);
        else
            guildDataManager.updateValue(Key.PINNED_THRESHOLD.string(), Long.toString(this.pinThreshold));
    }

    public RoleGUI modifierForUser(User user) {
        return roleModifiers.get(user);
    }

    public void removeRole(String name) {
        for (GameRole gr : new HashSet<>(roleMap.keySet())) {
            if (gr.getName().equalsIgnoreCase(name)) {
                roleMap.remove(gr);
                return;
            }
        }
    }

    public GameRole getGameRoleFromRole(Role r) {
        for (GameRole gr : roleMap.keySet()) {
            if (gr.getName().equalsIgnoreCase(r.getName()))
                return gr;
        }
        return null;
    }

    public Role getRoleFromGameRole(GameRole gameRole) {
        if (gameRole == null)
            return null;

        Role retrievedRole = roleMap.get(gameRole);

        if (retrievedRole != null)
            return retrievedRole;

        for (Role role : guild.getRoles()) {
            if (role.getName().equalsIgnoreCase(gameRole.getName())) {
                roleMap.put(gameRole, role);
                return role;
            }
        }
        return null;
    }

    public List<RoleGUI> getRoleModifiers() {
        return Collections.unmodifiableList(new ArrayList<>(new HashSet<>(roleModifiers.values())));
    }

    public void addRoleManagerForUser(User user, RoleGUI roleGUI) {
        if(roleModifiers.get(user) != null)
            roleModifiers.get(user).endSession();
        roleModifiers.put(user, roleGUI);
    }

    public void removeRoleManagerForUser(User user) {
        roleModifiers.remove(user);
    }

    public ArrayList<GameRole> getGameRoles() {
        return new ArrayList<>(roleMap.keySet());
    }

    public List<Role> getGameRolesAsRoles() {
        return new ArrayList<>(roleMap.values());
    }

    public void addGameRole(GameRole gameRole) {
        roleMap.put(gameRole, guild.getRolesByName(gameRole.getName(), true).get(0));
    }

    public void addGameRole(GameRole gameRole, Role role) {
        roleMap.put(gameRole, role);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        GuildDataManager jgm = GuildDataMapper.getInstance().getDataManager(guild);
        jgm.updateValue(Key.PREFIX.string(), prefix);
        this.prefix = prefix;
    }

    public int getPinThreshold() {
        return pinThreshold;
    }

    public void setPinThreshold(int amount) {
        GuildDataManager jgm = GuildDataMapper.getInstance().getDataManager(guild);
        this.pinThreshold = amount;
        jgm.updateValue(Key.PINNED_THRESHOLD.string(), Integer.toString(amount));
    }

    private enum Key {

        PREFIX("prefix"),
        GAMEROLES("gameroles"),
        NAME("name"),
        PINNED_THRESHOLD("pThreshold"),
        CREATOR("creatorId");


        private final String key;

        Key(String key) {
            this.key = key;
        }

        String string() {
            return key;
        }
    }
}
