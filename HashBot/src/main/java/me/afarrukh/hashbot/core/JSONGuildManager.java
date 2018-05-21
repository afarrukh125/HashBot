package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.gameroles.GameRole;
import net.dv8tion.jda.core.entities.Guild;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Iterator;

public class JSONGuildManager {
    private JSONObject jsonObject;
    private Guild guild;
    private File file;

    public JSONGuildManager(Guild guild) {
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

    private void load() {
        final JSONParser jsonParser = new JSONParser();
        Object obj;

        try {

            obj = jsonParser.parse(new FileReader(file));
            this.jsonObject = (JSONObject) obj;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            createGuildFile();
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createGuildFile() {
        JSONObject obj = new JSONObject();
        obj.put("name", guild.getName());
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

    public void addRole(String name, int red, int green, int blue) {
        JSONArray arr = (JSONArray) jsonObject.get("gameroles");
        JSONObject roleObj = new JSONObject();

        roleObj.put("name", name);
        roleObj.put("red", red);
        roleObj.put("green", green);
        roleObj.put("blue", blue);

        arr.add(roleObj);

        jsonObject.put("gameroles", arr);

        Bot.gameRoleManager.getGuildRoleManager(guild).getGameRoles().add(new GameRole(name, red, green, blue));
        try {

            FileWriter newFile = new FileWriter(file);
            newFile.write(jsonObject.toJSONString());
            newFile.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

        try {
            FileWriter newFile = new FileWriter(file);
            newFile.write(jsonObject.toJSONString());
            newFile.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
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




}
