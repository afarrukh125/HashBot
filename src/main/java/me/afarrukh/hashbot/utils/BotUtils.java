package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.gameroles.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BotUtils {

    /**
     * Deletes the last message from a bot in the channel (Hopes that it is the current JDA Bot user)
     *
     * @param evt The event object containing the text channel so we can retrieve the text message
     */
    public static void deleteLastMsg(MessageReceivedEvent evt) {
        for (Message m : evt.getChannel().getIterableHistory()) {
            if (m.getAuthor().getId().equals(evt.getJDA().getSelfUser().getId())) {
                m.delete().queueAfter(1500, TimeUnit.MILLISECONDS);
                break;
            }
        }
    }

    public static boolean isPinnedChannel(MessageReceivedEvent evt) {
        GuildDataManager jgm = GuildDataMapper.getInstance().getDataManager(evt.getGuild());
        return evt.getChannel().getId().equals(jgm.getValue("pinnedchannel"));
    }

    public static boolean isGameRole(Role r, Guild guild) {
        for (Object role : (JSONArray) GuildDataMapper.getInstance().getDataManager(guild).getValue("gameroles")) {
            JSONObject roleObj = (JSONObject) role;
            String roleName = (String) roleObj.get("name");
            if (roleName.equalsIgnoreCase(r.getName()))
                return true;
        }
        return false;
    }

    public static String[] createNumberEmojiArray() {
        String[] arr = new String[10];
        for (int i = 0; i < 9; i++) {
            arr[i] = (i + 1) + "⃣";
        }
        arr[9] = "\uD83D\uDD1F";

        return arr;
    }

    public static boolean isValidGameRole(Role r) {
        return (r.hasPermission(Permission.ADMINISTRATOR) || r.hasPermission(Permission.BAN_MEMBERS)
                || r.hasPermission(Permission.MANAGE_CHANNEL) || r.hasPermission(Permission.MANAGE_SERVER)
                || r.hasPermission(Permission.MESSAGE_MANAGE) || r.hasPermission(Permission.VIEW_AUDIT_LOGS));
    }

    public static String[] createStandardNumberEmojiArray() {

        return new String[]{
                ":one:",
                ":two:",
                ":three:",
                ":four:",
                ":five:",
                ":six:",
                ":seven:",
                ":eight:",
                ":nine:",
                ":keycap_ten:"
        };
    }

    public static int getMaxEntriesOnPage(List<GameRole> roleList, int page) {
        Iterator<GameRole> iter = roleList.iterator();

        int startIdx = 1 + ((page - 1) * 10); //The start role on that page eg page 2 would give 11
        int targetIdx = page * 10; //The last role on that page, eg page 2 would give 20
        int count = 1;
        int emojiCount = 0;
        while (iter.hasNext()) {
            GameRole gameRole = iter.next();
            if (count >= startIdx && count <= targetIdx) {
                emojiCount++;
            }
            if (count == targetIdx)
                break;

            count++;
        }
        return emojiCount;
    }

    /**
     * Returns a role object for a given guild and role name
     *
     * @param g    The guild to check
     * @param name The role name to check for
     * @return The role object if found, null if not found
     */
    public static Role getRoleByName(Guild g, String name) {
        //Checks if the game role exists in the guild
        for (Role r : g.getRoles()) {
            if (r.getName().equalsIgnoreCase(name))
                return r;
        }
        return null;
    }

    /**
     * Calculates the approximate memory usage of the bot
     *
     * @return The approximate memory usage in megabytes
     * @see me.afarrukh.hashbot.cli.commands.CheckMemoryCLI
     * @see me.afarrukh.hashbot.commands.management.bot.CheckMemoryCommand
     */
    public static long getMemoryUsage() {
        long memoryNow = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryDiff = memoryNow - Constants.INITIAL_MEMORY;
        memoryDiff /= (1024 * 1024); //Converting from bytes to kb to mb by dividing by 1024 twice

        return memoryDiff;
    }

}
