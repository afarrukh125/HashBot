package me.afarrukh.hashbot.data;

import net.dv8tion.jda.core.entities.Member;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class that manipulates the json object associated with an invoker object
 */

@SuppressWarnings("unchecked")
public class UserDataManager extends DataManager {

    private final Member member;

    public UserDataManager(Member m) {
        super();
        String guildId = m.getGuild().getId();
        String userId = m.getUser().getId();

        String filePath = "res/guilds/" +guildId+ "/users/" +userId+".txt";

        member = m;
        file = new File(filePath);

        if(file.exists())
            load();
        else {
            if(new File("res/guilds/" +guildId+ "/users").mkdirs()) {
                load();
            }
            else {
                load();
            }
        }
    }

    /**
     * Loads the JSON object from file into instance variable
     */
    public void load() {
        initialiseData();
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

    @Override
    /**
     * Fired when the user has no file created already, creates one with default values
     */
    @SuppressWarnings("unchecked")
    public void writePresets() {
        JSONObject obj = new JSONObject();
        obj.put("name", member.getUser().getName());
        obj.put("credit", 6000);
        obj.put("time", System.currentTimeMillis());
        obj.put("level", 1);
        obj.put("score", 0);

        try (FileWriter newFile = new FileWriter(file)) {
            newFile.write(obj.toJSONString());
            newFile.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Created file for user " +member.getUser().getName());
        System.out.println(obj);
    }
}
