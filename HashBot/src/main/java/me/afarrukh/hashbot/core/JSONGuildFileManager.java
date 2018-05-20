package me.afarrukh.hashbot.core;

import net.dv8tion.jda.core.entities.Guild;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class JSONGuildFileManager {
    private JSONObject jsonObject;
    private Guild guild;
    private File file;

    public JSONGuildFileManager(Guild guild) {
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

        try (FileWriter newFile = new FileWriter(file)) {
            newFile.write(obj.toJSONString());
            newFile.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
