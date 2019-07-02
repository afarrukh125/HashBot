package me.afarrukh.hashbot.data;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.core.entities.Guild;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This class manages data for a guild. This includes things like the bot prefixes, the pinned channel
 * (if set) and also the GameRoles.
 */
@SuppressWarnings("unchecked")
public class GuildDataManager extends DataManager {

    private final Guild guild;

    private static final String pinnedChannelKey = "pinnedchannel";
    private static final String pinnedMessages = "pinnedmessages";

    private static final String autoPinKey = "autopinchannels";

    private Map<String, String> pinnedMessageMap;
    private List<String> autoPinChannels;

    public GuildDataManager(Guild guild) {
        super();
        this.guild = guild;
        String guildId = guild.getId();

        pinnedMessageMap = new HashMap<>();
        autoPinChannels = new ArrayList<>();

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

        JSONArray pinnedChannelArr = (JSONArray) jsonObject.get(pinnedMessages);

        if(pinnedChannelArr == null) {
            pinnedChannelArr = new JSONArray();
            jsonObject.put(pinnedMessages, pinnedChannelArr);
            flushData();
        } else {
            for (Object o : pinnedChannelArr) {
                JSONObject obj = (JSONObject) o;
                for(Object o2: obj.keySet()) {
                    pinnedMessageMap.put(o2.toString(), (String) obj.get(o2));
                }
            }
        }

        JSONArray autoPinArray = (JSONArray) jsonObject.get(autoPinKey);
        if(autoPinArray == null) {
            autoPinArray = new JSONArray();
            jsonObject.put(autoPinKey, autoPinArray);
            flushData();
        } else {
            for(Object o: autoPinArray) {
                autoPinChannels.add(o.toString());
            }
        }
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public void writePresets() {
        JSONObject obj = new JSONObject();
        obj.put("name", guild.getName());
        obj.put("prefix", Constants.invokerChar);
        obj.put(pinnedChannelKey, "1"); // Default value

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

        Bot.gameRoleManager.getGuildRoleManager(guild).removeRole(name);
        flushData();
    }

    public void setPrefix(String prefix) {
        updateValue("prefix", prefix);
    }

    public void setPinnedChannel(String id) {
        updateValue(pinnedChannelKey, id);
    }

    public void unsetPinnedChannel() {
        updateValue(pinnedChannelKey, "");
    }

    public void addAsPinned(String originalId, String pinnedId) {
        pinnedMessageMap.put(originalId, pinnedId);

        JSONArray arr = (JSONArray) jsonObject.get(pinnedMessages);

        JSONObject object = new JSONObject();
        object.put(originalId, pinnedId);

        arr.add(object);

        jsonObject.put(pinnedMessages, arr);
        flushData();
    }

    public boolean isPinned(String id) {
        return pinnedMessageMap.get(id) != null;
    }

    /**
     * Returns the pinned channel ID for the guild held in this GuildDataManager object
     * @return A string of "1" if the guild has no pinned channel, otherwise returns the channel ID of the pinned channel
     */
    public String getPinnedChannelId() {
        if(jsonObject.get(pinnedChannelKey).equals(""))
            return "1";

        return (String) jsonObject.get(pinnedChannelKey);
    }

    public void updateValue(Object key, Object value) {
        jsonObject.put(key, value);
        flushData();
    }

    public void addAutoPinChannel(String channelId) {
        autoPinChannels.add(channelId);
        JSONArray arr = (JSONArray) jsonObject.get(autoPinKey);
        arr.add(channelId);
        jsonObject.put(autoPinKey, arr);
        flushData();

    }

    public void deletePinnedEntryByOriginal(String id) {
        JSONArray arr = (JSONArray) jsonObject.get(pinnedMessages);
        Iterator<Object> iter = arr.iterator();
        while(iter.hasNext()) {
            JSONObject obj = (JSONObject) iter.next();
            if(obj.keySet().contains(id)) {
                iter.remove();
                pinnedMessageMap.remove(id);
            }
        }
        jsonObject.put(pinnedMessages, arr);
        flushData();
    }

    public void deletePinnedEntryByNew(String id) {
        JSONArray arr = (JSONArray) jsonObject.get(pinnedMessages);
        Iterator<Object> iter = arr.iterator();
        while(iter.hasNext()) {
            JSONObject obj = (JSONObject) iter.next();
            for(Object o2: obj.keySet()) {
                if (id.equals(obj.get(o2))) {
                    iter.remove();
                    pinnedMessageMap.remove(o2.toString());
                }
            }
        }
        jsonObject.put(pinnedMessages, arr);
        flushData();
    }

    public String getPinnedMessageIdFromOriginalMessage(String s) {
        return pinnedMessageMap.get(s);
    }

    public Object getValue(Object key) {
        try {
            return jsonObject.get(key);
        } catch(NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks whether the message with the given id is a bot message that represents another message that was pinned.
     * @param id The id of the message
     * @return A boolean corresponding to whether the message is a bot message that represents a pinned message.
     */
    public boolean isBotPinMessage(String id) {
        return pinnedMessageMap.values().contains(id);
    }

    public List<String> getAutoPinChannels() {
        return autoPinChannels;
    }
}
