package me.afarrukh.hashbot.core;

import net.dv8tion.jda.core.entities.Member;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

/**
 * Class that manipulates the json object associated with an invoker object
 */

public class JSONFileManager {

    private JSONObject jsonObject;
    private File file;
    private Member member;

    public JSONFileManager(Member m) {
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
    private void load() {
        final JSONParser jsonParser = new JSONParser();
        Object obj;

        try {

            obj = jsonParser.parse(new FileReader(file));
            this.jsonObject = (JSONObject) obj;

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            createFile();
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void updateField(Object key, Object value) {
        jsonObject.put(key, value);
        try {

            FileWriter newFile = new FileWriter(file);
            newFile.write(jsonObject.toJSONString());
            newFile.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object getValue(Object key) {
        try {
            Object value = jsonObject.get(key);
            return value;
        } catch(NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Fired when the user has no file created already, creates one with default values
     */
    @SuppressWarnings("unchecked")
    public void createFile() {
        JSONObject obj = new JSONObject();
        obj.put("name", member.getUser().getName());
        obj.put("credit", 0);
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
