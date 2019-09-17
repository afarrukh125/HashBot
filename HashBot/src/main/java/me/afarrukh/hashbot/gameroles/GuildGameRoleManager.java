package me.afarrukh.hashbot.gameroles;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.DataManager;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
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

    private static final String pinnedKey = "pThreshold";

    private final Map<GameRole, Role> roleMap;
    private final Map<Long, RoleGUI> roleModifiers;
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
        DataManager jgm = GuildDataMapper.getInstance().getDataManager(guild);
        JSONArray arr = (JSONArray) jgm.getValue("gameroles");
        //noinspection unchecked
        Iterator<Object> iter = arr.iterator();
        while (iter.hasNext()) {
            JSONObject roleObj = (JSONObject) iter.next();
            String roleName = (String) roleObj.get("name");
            List<Role> roleList = guild.getRolesByName(roleName, true);
            if (!roleList.isEmpty()) {
                Role role = roleList.get(0);
                GameRole gameRole = new GameRole(roleName, (String) roleObj.get("creatorId"));
                roleMap.put(gameRole, role);
            } else
                iter.remove();
        }
        jgm.updateValue("gameroles", arr);
        String prefix = (String) jgm.getValue("prefix");
        if (prefix != null)
            this.prefix = prefix;
        else
            jgm.updateValue("prefix", Constants.invokerChar);

        // Setting the pinned threshold
        String threshold = (String) jgm.getValue(pinnedKey);
        if (threshold != null)
            this.pinThreshold = Integer.parseInt(threshold);
        else
            jgm.updateValue(pinnedKey, Long.toString(this.pinThreshold));
    }

    public RoleGUI modifierForUser(User user) {
        return roleModifiers.get(user.getIdLong());
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

    public Role getRoleFromGameRole(GameRole gr) {
        if (gr == null)
            return null;

        Role role = roleMap.get(gr);

        if (role != null)
            return role;

        for (Role r : guild.getRoles()) {
            if (r.getName().equalsIgnoreCase(gr.getName())) {
                roleMap.put(gr, r);
                return r;
            }
        }
        return null;
    }

    public Map<Long, RoleGUI> getRoleModifiers() {
        return roleModifiers;
    }

    public ArrayList<GameRole> getGameRoles() {
        return new ArrayList<>(roleMap.keySet());
    }

    /**
     * Returns a linked list of roles.
     *
     * @return The list of game roles for the guild as roles.
     */
    public List<Role> getGameRolesAsRoles() {
        return new ArrayList<>(roleMap.values());
    }

    public void addGameRole(GameRole gameRole) {
        roleMap.put(gameRole, guild.getRolesByName(gameRole.getName(), true).get(0));
    }

    /**
     * This one should be used during runtime
     *
     * @param gameRole The game role to be added
     * @param role     The role this game role is to be mapped to
     */
    public void addGameRole(GameRole gameRole, Role role) {
        roleMap.put(gameRole, role);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        GuildDataManager jgm = GuildDataMapper.getInstance().getDataManager(guild);
        jgm.updateValue("prefix", prefix);
        this.prefix = prefix;
    }

    public int getPinThreshold() {
        return pinThreshold;
    }

    public void setPinThreshold(int amount) {
        GuildDataManager jgm = GuildDataMapper.getInstance().getDataManager(guild);
        this.pinThreshold = amount;
        jgm.updateValue(pinnedKey, Integer.toString(amount));
    }
}
