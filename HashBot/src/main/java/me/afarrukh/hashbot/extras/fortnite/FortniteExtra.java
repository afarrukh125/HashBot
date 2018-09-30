package me.afarrukh.hashbot.extras.fortnite;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.DataManager;
import me.afarrukh.hashbot.extras.Extra;
import me.afarrukh.hashbot.graphics.Text;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FortniteExtra extends DataManager implements Extra {

    private Guild guild;
    private Map<Member, FortniteEntry> memberToPlayerMap;
    private TextChannel fortniteChannel;

    private Message infoMessage;

    private Timer updateTimer;

    public FortniteExtra(Guild guild) {
        super();
        this.guild = guild;
        memberToPlayerMap = new HashMap<>();
        fortniteChannel = null;

        String guildId = guild.getId();

        String filePath = "res/guilds/" +guildId+ "/data/" +"fortnitedata.json";

        file = new File(filePath);
        updateTimer = new Timer();
        updateTimer.schedule(new FortniteExtra.UpdateTimer(), 60*1000*Constants.FTN_REFRESH_MIN, 60*1000*Constants.FTN_REFRESH_MIN);

        decideFile();

        fillMap();
        initChannel();
    }

    private void update() {
        if(fortniteChannel == null)
            return;

        if(infoMessage != null)
            infoMessage.editMessage(getFortniteMessage()).queue();
        else
            infoMessage = fortniteChannel.sendMessage(getFortniteMessage()).complete();
    }

    private void initChannel() {
        TextChannel channelToSearch = guild.getTextChannelById((String)jsonObject.get("fortnitechannel"));
        if(channelToSearch != null)
            fortniteChannel = channelToSearch;
        else
            return;

        for(Message m: fortniteChannel.getIterableHistory()) {
            if(m.getAuthor().getId().equalsIgnoreCase(m.getGuild().getJDA().getSelfUser().getId())) {
                infoMessage = m;
                break;
            }
        }
        if(infoMessage == null) {
            infoMessage = fortniteChannel.sendMessage(getFortniteMessage()).complete();
        }
    }

    @SuppressWarnings("unchecked")
    public void addUser(MessageReceivedEvent evt, String params) {
        Member member = evt.getMember();
        String[] tokens = params.split(" ");
        String platform = tokens[0];
        String userName = tokens[1];

        FortniteEntry entry = new FortniteEntry(member, userName, platform);

        JSONArray userList = getUsersAsJSONArray();

        JSONObject userObject = new JSONObject();
        String userId = member.getUser().getId();

        userObject.put("memberid", userId);
        userObject.put("fortusername", userName);
        userObject.put("platform", platform);

        Iterator<Object> iter = userList.iterator();

        while(iter.hasNext()) {
            JSONObject iteratedObject = (JSONObject) iter.next();
            if(iteratedObject.get("memberid").equals(member.getUser().getId())) {
                iter.remove();
                memberToPlayerMap.remove(member);
            }
        }
        userList.add(userObject);
        updateValue("fortniteusers", userList);

        memberToPlayerMap.put(member, entry);

        update();
    }

    private void fillMap() {
        if(getUsersAsJSONArray().isEmpty())
            return;
        for(Object o: getUsersAsJSONArray()) {
            JSONObject userObject = (JSONObject) o;
            Member m = guild.getMemberById((String) userObject.get("memberid"));
            String userName = (String) userObject.get("fortusername");
            String platform = (String) userObject.get("platform");

            FortniteEntry entry = new FortniteEntry(m, userName, platform);

            memberToPlayerMap.put(guild.getMemberById((String) userObject.get("memberid")), entry);
        }
    }

    public TextChannel getFortniteChannel() {
        return fortniteChannel;
    }

    public Map<Member, FortniteEntry> getMemberToPlayerMap() {
        return memberToPlayerMap;
    }

    private MessageEmbed getFortniteMessage(){
        EmbedBuilder eb = new EmbedBuilder().setColor(Constants.EMB_COL);
        eb.setThumbnail(guild.getIconUrl());

        Collection<FortniteEntry> values = memberToPlayerMap.values();

        if(values.isEmpty())
            eb.appendDescription("No users setup for this guild. Use "
                    + Bot.gameRoleManager.getGuildRoleManager(guild).getPrefix() + "ftnreg <pc/ps4> <name> to register to this guild.");
        else {
            for (FortniteEntry entry : memberToPlayerMap.values()) {
                eb.appendDescription(entry.getUserName() + " (" + entry.getPlatform() + ")\n");
            }
        }

        return eb.build();
    }

    private JSONArray getUsersAsJSONArray() {
        return (JSONArray) getValue("fortniteusers");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writePresets() {
        JSONObject obj = new JSONObject();
        obj.put("fortnitechannel", "-1");

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

    public void setFortniteChannel(TextChannel channel) {
        fortniteChannel = channel;
        updateValue("fortnitechannel", channel.getId());
        initChannel();
        update();
    }

    public Message getInfoMessage() {
        return infoMessage;
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

    public void processEvent(MessageDeleteEvent evt) {
        if(evt.getMessageId().equalsIgnoreCase(infoMessage.getId())) {
            infoMessage = evt.getTextChannel().sendMessage(getFortniteMessage()).complete();
        }
    }

    private class UpdateTimer extends TimerTask {
        @Override
        public void run() {
            update();
        }
    }

}
