package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.JSONGuildManager;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.gameroles.GameRole;
import me.afarrukh.hashbot.gameroles.RoleAdder;
import me.afarrukh.hashbot.gameroles.RoleBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class BotUtils {

    /**
     * Deletes the last message from a bot in the channel (Hopes that it is the current JDA Bot user)
     * @param evt The event object containing the text channel so we can retrieve the text message
     */
    public static void deleteLastMsg(MessageReceivedEvent evt) {
        for(Message m: evt.getTextChannel().getIterableHistory()) {
            if(m.getAuthor().getId().equals(evt.getJDA().getSelfUser().getId())) {
                m.delete().queueAfter(1500, TimeUnit.MILLISECONDS);
                break;
            }
        }
    }

    public static boolean isPinnedChannel(MessageReceivedEvent evt) {
        JSONGuildManager jgm = new JSONGuildManager(evt.getGuild());
        if(evt.getTextChannel().getId().equals(jgm.getValue("pinnedchannel")))
            return true;
        else
            return false;
    }

    public static boolean isGameRole(Role r, Guild guild) {
        for(Object role: (JSONArray) new JSONGuildManager(guild).getValue("gameroles")) {
            JSONObject roleObj = (JSONObject) role;
            String roleName = (String) roleObj.get("name");
            if(roleName.equalsIgnoreCase(r.getName()))
                return true;
        }
        return false;
    }

    public static void createRole(Guild g, RoleBuilder rb) {
        //Validating if the role is already existing in the guild
        if(rb.getColor().getBlue() == 0 && rb.getColor().getGreen() == 0 && rb.getColor().getRed() == 0) {
            rb.message.editMessage(EmbedUtils.getInvalidRoleEmbed(rb)).queue();
            return;
        }

        if(rb.roleName.equals("")) {
            rb.message.editMessage(EmbedUtils.getInvalidRoleEmbed(rb)).queue();
            return;
        }

        if(rb.getColor().getBlue() > 255 || rb.getColor().getGreen() > 255 || rb.getColor().getRed() > 255) {
            rb.message.editMessage(EmbedUtils.getInvalidRoleEmbed(rb)).queue();
            return;
        }

        for(Role r: g.getRoles()) {
            if (r.getName().equalsIgnoreCase(rb.roleName)) {
                rb.message.editMessage(EmbedUtils.getRoleExistsEmbed(rb)).queue();
                return;
            }
        }
        Member m = g.getMemberById(rb.getUser().getId());
        for(Role r: m.getRoles())
            if (r.getName().equalsIgnoreCase(rb.roleName)) {
                rb.message.editMessage(EmbedUtils.getRoleExistsEmbed(rb)).queue();
                return;
            }

        Role newRole = g.getController().createRole().complete();

        String cap = rb.roleName.substring(0, 1).toUpperCase() + rb.roleName.substring(1);

        try {
            newRole.getManager().setName(cap).setMentionable(true).setHoisted(false).setColor(rb.getColor())
                    .queue();
        } catch(IllegalArgumentException e) {
            rb.message.editMessage(EmbedUtils.getInvalidRoleEmbed(rb)).queue();
            newRole.delete().queue();
            return;
        }

        g.getController().addSingleRoleToMember(m, newRole).queue();
        JSONGuildManager jgm = new JSONGuildManager(g);
        jgm.addRole(cap, rb.getUser().getId());
        Invoker inv = new Invoker(rb.getGuild().getMemberById(rb.getUser().getId()));
        inv.addCredit(-Constants.ROLE_CREATE_AMOUNT);
    }

    public static String[] createNumberEmojiArray() {
        String[] arr = new String[10];
        for(int i = 0; i<9; i++) {
            arr[i] = (i+1)+"⃣";
        }
        arr[9] = "\uD83D\uDD1F";

        return arr;
    }

    public static String[] createStandardNumberEmojiArray() {
        String[] arr = new String[] {
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

        return arr;
    }

    public static int getMaxEntriesOnPage(RoleAdder ra, int page) {
        ArrayList<GameRole> roleList = Bot.gameRoleManager.getGuildRoleManager(ra.guild).getGameRoles();
        Iterator<GameRole> iter = roleList.iterator();

        int startIdx = 1 + ((page-1)*10); //The start role on that page eg page 2 would give 11
        int targetIdx = page * 10; //The last role on that page, eg page 2 would give 20
        int count = 1;
        int emojiCount = 0;
        while(iter.hasNext()) {
            GameRole gameRole = iter.next();
            if(count >= startIdx && count<=targetIdx) {
                emojiCount++;
            }
            if(count==targetIdx)
                break;

            count++;
        }
        return emojiCount;
    }

    public static void addRoleToMember(RoleAdder ra) {
        Member m = ra.guild.getMemberById(ra.user.getId());
        Role roleToAdd = Bot.gameRoleManager.getGuildRoleManager(ra.guild).getRoleFromGameRole(ra.desiredRole);
        ra.guild.getController().addSingleRoleToMember(m, roleToAdd).queue();
    }


}
