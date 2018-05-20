package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.JSONGuildManager;
import me.afarrukh.hashbot.gameroles.GameRole;
import me.afarrukh.hashbot.gameroles.RoleBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    public static boolean isGameRole(Role r, MessageReceivedEvent evt) {
        for(Object role: (JSONArray) new JSONGuildManager(evt.getGuild()).getValue("gameroles")) {
            JSONObject roleObj = (JSONObject) role;
            if(roleObj.get("name").equals(r.getName()))
                return true;
        }
        return false;
    }

    public static void createRole(Guild g, RoleBuilder rb) {
        Role newRole = g.getController().createRole().complete();
        Member m = g.getMemberById(rb.user.getId());

        String cap = rb.roleName.substring(0, 1).toUpperCase() + rb.roleName.substring(1);

        newRole.getManager().setName(cap).setMentionable(true).setHoisted(false).setColor(rb.color)
                .queue();

        g.getController().addSingleRoleToMember(m, newRole).queue();
    }


}
