package me.afarrukh.hashbot.data;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.gameroles.GameRole;
import net.dv8tion.jda.core.entities.Guild;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * This class manages data for a guild. This includes things like the bot prefixes, the pinned channel
 * (if set) and also the GameRoles.
 */
@SuppressWarnings("unchecked")
public class GuildDataManager extends DataManager {

    private final Guild guild;

    public GuildDataManager(Guild guild) {
        super();
        this.guild = guild;
        String guildId = guild.getId();

        String filePath = "res/guilds/" +guildId+ "/data/" +"data.json";

        file = new File(filePath);

        if(file.exists())
            load();
        else {
            if(new File("res/guilds/"+guildId+"/data").mkdirs()) {
                load();
            }
            else {
                load();
            }
        }
    }

    public void load() {
        initialiseData();
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public void writePresets() {
        JSONObject obj = new JSONObject();
        obj.put("name", guild.getName());
        obj.put("prefix", Constants.invokerChar);
        obj.put("pinnedchannel", "");

        JSONArray gameRoles = new JSONArray();

        obj.put("gameroles", gameRoles);

        try (FileWriter newFile = new FileWriter(file)) {
            newFile.write(obj.toJSONString());
            newFile.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(obj);
    }

    /**
     * Updates the JSON file with the current state of the JSON file after updating the gameroles array
     * @param name The name of the role
     * @param creatorId The ID of the user who created the game role
     */
    public void addRole(String name, String creatorId) {
        JSONArray arr = (JSONArray) jsonObject.get("gameroles");
        JSONObject roleObj = new JSONObject();

        roleObj.put("name", name);
        roleObj.put("creatorId", creatorId);

        arr.add(roleObj);

        jsonObject.put("gameroles", arr);

        Bot.gameRoleManager.getGuildRoleManager(guild).getGameRoles().add(new GameRole(name, creatorId));
        flushData();
    }

    /**
     * Removes the role from the JSON object and flushes it to file
     * @param name The name of the role to be removed from the JSON file
     */
    public void removeRole(String name) {
        JSONArray arr = (JSONArray) jsonObject.get("gameroles");
        Iterator<Object> iter = arr.iterator();
        while(iter.hasNext()) {
            JSONObject obj = (JSONObject) iter.next();
            String roleName = (String) obj.get("name");
            if(roleName.equalsIgnoreCase(name)) {
                iter.remove();
            }
        }
        jsonObject.put("gameroles", arr);

        Bot.gameRoleManager.getGuildRoleManager(guild).remove(name);
        flushData();
    }

    public void setPrefix(String prefix) {
        updateValue("prefix", prefix);
    }

    public void setPinnedChannel(String id) {
        updateValue("pinnedchannel", id);
    }

    public void unsetPinnedChannel() {
        updateValue("pinnedchannel", "");
    }

    public void updateValue(Object key, Object value) {
        jsonObject.put(key, value);
        flushData();
    }

    public Object getValue(Object key) {
        try {
            return jsonObject.get(key);
        } catch(NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }




}
