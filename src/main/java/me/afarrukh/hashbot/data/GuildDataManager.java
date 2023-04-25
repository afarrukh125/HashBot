package me.afarrukh.hashbot.data;

import me.afarrukh.hashbot.config.Constants;
import net.dv8tion.jda.api.entities.Guild;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This class manages data for a guild. This includes things like the bot prefixes, the pinned channel
 */
@SuppressWarnings("unchecked")
public class GuildDataManager extends DataManager {
    private static final Logger LOG = LoggerFactory.getLogger(GuildDataManager.class);
    private static final String pinnedChannelKey = "pinnedchannel";
    private static final String pinnedMessages = "pinnedmessages";
    private static final String autoPinKey = "autopinchannels";
    private final Guild guild;
    private final Map<String, String> pinnedMessageMap;
    private final List<String> autoPinChannels;

    public GuildDataManager(Guild guild) {
        super();
        this.guild = guild;
        String guildId = guild.getId();

        pinnedMessageMap = new HashMap<>();
        autoPinChannels = new ArrayList<>();

        String filePath = "res/guilds/" + guildId + "/data/" + "data.json";

        file = new File(filePath);

        if (file.exists()) load();
        else {
            if (new File("res/guilds/" + guildId + "/data").mkdirs()) {
                load();
            } else {
                load();
            }
        }
    }

    public void load() {
        initialiseData();

        JSONArray pinnedChannelArr = (JSONArray) jsonObject.get(pinnedMessages);

        if (pinnedChannelArr == null) {
            pinnedChannelArr = new JSONArray();
            jsonObject.put(pinnedMessages, pinnedChannelArr);
            flushData();
        } else {
            for (Object o : pinnedChannelArr) {
                JSONObject obj = (JSONObject) o;
                for (Object o2 : obj.keySet()) {
                    pinnedMessageMap.put(o2.toString(), (String) obj.get(o2));
                }
            }
        }

        JSONArray autoPinArray = (JSONArray) jsonObject.get(autoPinKey);
        if (autoPinArray == null) {
            autoPinArray = new JSONArray();
            jsonObject.put(autoPinKey, autoPinArray);
            flushData();
        } else {
            for (Object o : autoPinArray) {
                autoPinChannels.add(o.toString());
            }
        }
    }

    public void writePresets() {
        JSONObject obj = new JSONObject();
        obj.put("name", guild.getName());
        obj.put("prefix", Constants.invokerChar);
        obj.put(pinnedChannelKey, "1"); // Default value

        try (FileWriter newFile = new FileWriter(file)) {
            newFile.write(obj.toJSONString());
            newFile.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("Wrote presets {}", obj);
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
     */
    public String getPinnedChannelId() {
        if (jsonObject == null) return null;

        if (jsonObject.get(pinnedChannelKey) == null
                || jsonObject.get(pinnedChannelKey).equals("")) return null;

        return (String) jsonObject.get(pinnedChannelKey);
    }

    public void updateValue(Object key, Object value) {
        jsonObject.put(key, value);
        flushData();
    }

    public void deletePinnedEntryByOriginal(String id) {
        JSONArray arr = (JSONArray) jsonObject.get(pinnedMessages);
        Iterator<Object> iter = arr.iterator();
        while (iter.hasNext()) {
            JSONObject obj = (JSONObject) iter.next();
            if (obj.containsKey(id)) {
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
        while (iter.hasNext()) {
            JSONObject obj = (JSONObject) iter.next();
            for (Object o2 : obj.keySet()) {
                if (id.equals(obj.get(o2))) {
                    iter.remove();
                    pinnedMessageMap.remove(o2.toString());
                }
            }
        }
        jsonObject.put(pinnedMessages, arr);
        flushData();
    }

    public Object getValue(Object key) {
        try {
            return jsonObject.get(key);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks whether the message with the given id is a bot message that represents another message that was pinned.
     *
     * @param id The id of the message
     * @return A boolean corresponding to whether the message is a bot message that represents a pinned message.
     */
    public boolean isBotPinMessage(String id) {
        return pinnedMessageMap.containsValue(id);
    }

    public List<String> getAutoPinChannels() {
        return autoPinChannels;
    }
}
