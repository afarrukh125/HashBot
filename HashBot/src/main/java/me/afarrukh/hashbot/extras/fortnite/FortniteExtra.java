package me.afarrukh.hashbot.extras.fortnite;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.DataManager;
import me.afarrukh.hashbot.extras.Extra;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FortniteExtra extends DataManager implements Extra {

    private final Guild guild;
    private final List<FortniteEntry> entriesList;
    private TextChannel fortniteChannel;

    private Message infoMessage;

    private final Comparator<FortniteEntry> entrySorter;

    private final Timer updateTimer;

    public FortniteExtra(Guild guild) {
        super();
        this.guild = guild;
        entriesList = new ArrayList<>();
        fortniteChannel = null;

        String guildId = guild.getId();

        String filePath = "res/guilds/" +guildId+ "/data/" +"fortnitedata.json";

        file = new File(filePath);
        updateTimer = new Timer();
        updateTimer.schedule(new FortniteExtra.UpdateTimer(), 60*1000*Constants.FTN_REFRESH_MIN, 60*1000*Constants.FTN_REFRESH_MIN);

        decideFile();

        fillMap();

        entrySorter = (o1, o2) -> {
            if(o1.getLifeTimeStatistic().getKd() > o2.getLifeTimeStatistic().getKd())
                return -1;
            else if(o1.getLifeTimeStatistic().getKd() == o2.getLifeTimeStatistic().getKd())
                return 0;
            return 1;
        };

        initChannel();
        update();
    }

    private void update() {
        if(fortniteChannel == null)
            return;

        if(infoMessage != null)
            infoMessage.editMessage(getFortniteMessage()).queue();
        else
            infoMessage = fortniteChannel.sendMessage(getFortniteMessage()).complete();
    }

    private MessageEmbed getFortniteMessage(){
        EmbedBuilder eb = new EmbedBuilder().setColor(Constants.EMB_COL);

        if(entriesList.isEmpty())
            eb.appendDescription("No users setup for this guild. Use "
                    + Bot.gameRoleManager.getGuildRoleManager(guild).getPrefix() + "ftnreg <pc/ps4> <name> to register to this guild.");
        else {
            entriesList.sort(entrySorter);
            for (FortniteEntry entry : entriesList) {
                //Lifetime
                StringBuilder builder = new StringBuilder();
//                builder.append("Wins: ").append(entry.getLifeTimeStatistic().getWins()).append("\n");
//                builder.append("K/D: ").append(entry.getLifeTimeStatistic().getKd()).append("\n");
//                builder.append("Win%: ").append(entry.getLifeTimeStatistic().getWinPercentage()).append("\n");
//                builder.append("Kills: ").append(entry.getLifeTimeStatistic().getKills()).append("\n");
//                builder.append("Matches: ").append(entry.getLifeTimeStatistic().getMatches());
//                eb.addField(new MessageEmbed.Field(entry.getUserName() + " Lifetime", builder.toString(), true));

                //Solo
                builder = new StringBuilder();
                builder.append("Wins: ").append(entry.getSoloStatistic().getWins()).append("\n");
                builder.append("K/D: ").append(entry.getSoloStatistic().getKd()).append("\n");
                builder.append("Win%: ").append(entry.getSoloStatistic().getWinPercentage()).append("\n");
                builder.append("Kills: ").append(entry.getSoloStatistic().getKills()).append("\n");
                builder.append("Matches: ").append(entry.getSoloStatistic().getMatches());
                eb.addField(new MessageEmbed.Field(entry.getUserName()+ " Solo", builder.toString(), true));

                //Duo
                builder = new StringBuilder();
                builder.append("Wins: ").append(entry.getDuoStatistic().getWins()).append("\n");
                builder.append("K/D: ").append(entry.getDuoStatistic().getKd()).append("\n");
                builder.append("Win%: ").append(entry.getDuoStatistic().getWinPercentage()).append("\n");
                builder.append("Kills: ").append(entry.getDuoStatistic().getKills()).append("\n");
                builder.append("Matches: ").append(entry.getDuoStatistic().getMatches());
                eb.addField(new MessageEmbed.Field(entry.getUserName() + " Duo", builder.toString(), true));

                //Squad
                builder = new StringBuilder();
                builder.append("Wins: ").append(entry.getSquadStatistic().getWins()).append("\n");
                builder.append("K/D: ").append(entry.getSquadStatistic().getKd()).append("\n");
                builder.append("Win%: ").append(entry.getSquadStatistic().getWinPercentage()).append("\n");
                builder.append("Kills: ").append(entry.getSquadStatistic().getKills()).append("\n");
                builder.append("Matches: ").append(entry.getSquadStatistic().getMatches()).append("\n");
                eb.addField(new MessageEmbed.Field(entry.getUserName() + " Squad", builder.toString(), true));

            }
        }

        return eb.build();
    }

    private void initChannel() {
        TextChannel channelToSearch = guild.getTextChannelById((String) jsonObject.get("fortnitechannel"));
        if (getValue("fortnitechannel").equals("-1")) {
            System.out.println("<" +guild.getName()+ "> Server does not have this channel setup");
            return;
        }
        if (channelToSearch != null) {
            fortniteChannel = channelToSearch;
        }
        else
            return;

        for(Message m: fortniteChannel.getIterableHistory()) {
            if (m.getAuthor().getId().equalsIgnoreCase(m.getGuild().getJDA().getSelfUser().getId())) {
                infoMessage = m;
                break;
            }
        }

        if(infoMessage == null) {
            infoMessage = fortniteChannel.sendMessage(getFortniteMessage()).complete();
        }
    }

    @SuppressWarnings("unchecked")
    public void addUser(MessageReceivedEvent evt, String platform, String userName) {
        Member member = evt.getMember();

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
                if(getEntryFromMember(member) != null)
                    entriesList.remove(getEntryFromMember(member));
            }
        }
        userList.add(userObject);
        updateValue("fortniteusers", userList);

        entriesList.add(entry);

        update();
    }

    public void removeUser(MessageReceivedEvent evt) {
        FortniteEntry entry = getEntryFromMember(evt.getMember());
        if(entry == null) {
            evt.getTextChannel().sendMessage("You do not have any entries setup.").queue();
            return;
        }
        JSONArray userList = getUsersAsJSONArray();
        Iterator iter = userList.iterator();
        while(iter.hasNext()) {
            JSONObject userObject = (JSONObject) iter.next();
            if(userObject.get("memberid").equals(entry.getMember().getUser().getId())) {
                iter.remove();
            }
        }
        entriesList.remove(entry);
        evt.getTextChannel().sendMessage("Removed entry for " +evt.getMember().getEffectiveName()).queue();
        updateValue("fortniteusers", userList);
        update();
    }

    private FortniteEntry getEntryFromMember(Member m) {
        for(FortniteEntry entry: entriesList) {
            if(entry.getMember().equals(m)) {
                return entry;
            }
        }
        return null;
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

            entriesList.add(entry);
        }
    }

    public TextChannel getFortniteChannel() {
        return fortniteChannel;
    }

    private JSONArray getUsersAsJSONArray() {
        return (JSONArray) getValue("fortniteusers");
    }

    @Override
    @SuppressWarnings({"unchecked", "MismatchedQueryAndUpdateOfCollection"})
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
        unsetFortniteChannel();
        updateValue("fortnitechannel", channel.getId());
        initChannel();
    }

    public void unsetFortniteChannel() {
        fortniteChannel = null;
        updateValue("fortnitechannel", "-1");
        if(infoMessage != null)
            infoMessage.delete().queue();
        infoMessage = null;
    }

    private void updateEntries() {
        for(FortniteEntry entry: entriesList) {
            entry.updateEntry();
        }
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

    public void processEvent(GuildMessageDeleteEvent evt) {
        try {
            if (evt.getMessageId().equalsIgnoreCase(infoMessage.getId())) {
                infoMessage = evt.getChannel().sendMessage(getFortniteMessage()).complete();
            }
        } catch(NullPointerException ignore) {}
    }

    public void processEvent(MessageReceivedEvent evt) {
        if(evt.getTextChannel().equals(getFortniteChannel()) && !evt.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            try {
                evt.getMessage().delete().queue();
            } catch(InsufficientPermissionException ignore) {}
        }
    }

    private void sortEntries() {
        entriesList.sort(entrySorter);
    }

    private class UpdateTimer extends TimerTask {
        @Override
        public void run() {
            updateEntries();
            sortEntries();
            update();
        }
    }

}
