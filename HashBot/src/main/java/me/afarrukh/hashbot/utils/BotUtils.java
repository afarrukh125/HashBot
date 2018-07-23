package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.gameroles.*;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Iterator;
import java.util.List;
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
        GuildDataManager jgm = new GuildDataManager(evt.getGuild());
        return evt.getTextChannel().getId().equals(jgm.getValue("pinnedchannel"));
    }

    public static boolean isGameRole(Role r, Guild guild) {
        for(Object role: (JSONArray) new GuildDataManager(guild).getValue("gameroles")) {
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
        GuildDataManager jgm = new GuildDataManager(g);
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

        return new String[] {
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

    public static void deleteRole(RoleDeleter rd) {
        Role role = Bot.gameRoleManager.getGuildRoleManager(rd.getGuild()).getRoleFromGameRole(rd.getRoleToBeDeleted());
        role.delete().queue();
    }

    public static int getMaxUserRolesOnPage(RoleRemover rr, int page) {
        Iterator<GameRole> iter = new Invoker(rr.getGuild().getMember(rr.getUser())).getGameRoles().iterator();

        int startIdx = 1 + ((page-1)*10); //The start role on that page eg page 2 would give 11
        int targetIdx = page * 10; //The last role on that page, eg page 2 would give 20
        int count = 1;
        int emojiCount = 0;
        while(iter.hasNext()) {
            iter.next();
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
        Member m = ra.getGuild().getMemberById(ra.getUser().getId());
        Role roleToAdd = Bot.gameRoleManager.getGuildRoleManager(ra.getGuild()).getRoleFromGameRole(ra.getDesiredRole());
        ra.getGuild().getController().addSingleRoleToMember(m, roleToAdd).queue();
    }

    public static void removeRoleFromMember(RoleRemover rr, GameRole desiredRole) {
        Member m = rr.getGuild().getMemberById(rr.getUser().getId());
        Role roleToRemove = Bot.gameRoleManager.getGuildRoleManager(rr.getGuild()).getRoleFromGameRole(desiredRole);
        rr.getGuild().getController().removeSingleRoleFromMember(m, roleToRemove).queue();
    }

    public static boolean doesRoleExist(Guild g, String name) {
        //Checks if the game role exists in the guild
        for(Role r: g.getRoles()) {
            if(r.getName().equalsIgnoreCase(name))
                    return true;
        }
        return false;
    }


}
