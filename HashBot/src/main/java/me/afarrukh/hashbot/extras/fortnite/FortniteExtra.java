package me.afarrukh.hashbot.extras.fortnite;

import me.afarrukh.hashbot.data.DataManager;
import me.afarrukh.hashbot.extras.Extra;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FortniteExtra extends DataManager implements Extra {

    private Guild guild;
    private Map<Member, String> memberToPlayerMap;
    private TextChannel fortniteChannel;

    public FortniteExtra(Guild guild) {
        super();
        this.guild = guild;
        memberToPlayerMap = new HashMap<>();
        fortniteChannel = null;

        String guildId = guild.getId();

        String filePath = "res/guilds/" +guildId+ "/data/" +"fortnitedata.json";

        file = new File(filePath);

        decideFile();

        fillMap();
        initChannel();
    }

    private void fillMap() {
        if(getUsersAsJSONArray().isEmpty())
            return;
        for(Object o: getUsersAsJSONArray()) {
            JSONObject userObject = (JSONObject) o;
            memberToPlayerMap.put(guild.getMemberById((String) userObject.get("memberid")), (String)userObject.get("fortusername"));
        }
    }

    private void initChannel() {
        TextChannel channelToSearch = guild.getTextChannelById((Long) jsonObject.get("fortnitechannel"));
        if(channelToSearch != null)
            fortniteChannel = channelToSearch;
    }

    private JSONArray getUsersAsJSONArray() {
        return (JSONArray) getValue("fortniteusers");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writePresets() {
        JSONObject obj = new JSONObject();
        obj.put("fortnitechannel", "");

        JSONArray fortniteUsers = new JSONArray();

        obj.put("fortniteusers", fortniteUsers);

        try (FileWriter newFile = new FileWriter(file)) {
            newFile.write(obj.toJSONString());
            newFile.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(obj);
    }

    @Override
    public void load() {
        initialiseData();
    }

    @Override
    public Object getValue(Object key) {
        try {
            return jsonObject.get(key);
        } catch(NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateValue(Object key, Object value) {
        jsonObject.put(key, value);
        flushData();
    }

    private void decideFile() {
        if(file.exists())
            load();
        else {
            if(new File("res/guilds/"+guild.getId()+"/data").mkdirs()) {
                load();
            }
            else {
                load();
            }
        }
    }

}
