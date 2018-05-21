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
        if(rb.color.getBlue() == 0 && rb.color.getGreen() == 0 && rb.color.getRed() == 0) {
            rb.message.editMessage(EmbedUtils.getInvalidRoleEmbed(rb)).queue();
            return;
        }

        if(rb.roleName.equals("")) {
            rb.message.editMessage(EmbedUtils.getInvalidRoleEmbed(rb)).queue();
            return;
        }

        if(rb.color.getBlue() > 255 || rb.color.getGreen() > 255 || rb.color.getRed() > 255) {
            rb.message.editMessage(EmbedUtils.getInvalidRoleEmbed(rb)).queue();
            return;
        }

        for(Role r: g.getRoles()) {
            if (r.getName().equalsIgnoreCase(rb.roleName)) {
                rb.message.editMessage(EmbedUtils.getRoleExistsEmbed(rb)).queue();
                return;
            }
        }
        Member m = g.getMemberById(rb.user.getId());
        for(Role r: m.getRoles())
            if (r.getName().equalsIgnoreCase(rb.roleName)) {
                rb.message.editMessage(EmbedUtils.getRoleExistsEmbed(rb)).queue();
                return;
            }

        Role newRole = g.getController().createRole().complete();

        String cap = rb.roleName.substring(0, 1).toUpperCase() + rb.roleName.substring(1);

        try {
            newRole.getManager().setName(cap).setMentionable(true).setHoisted(false).setColor(rb.color)
                    .queue();
        } catch(IllegalArgumentException e) {
            rb.message.editMessage(EmbedUtils.getInvalidRoleEmbed(rb)).queue();
            newRole.delete().queue();
            return;
        }

        g.getController().addSingleRoleToMember(m, newRole).queue();
        JSONGuildManager jgm = new JSONGuildManager(g);
        jgm.addRole(rb.roleName, rb.color.getRed(), rb.color.getGreen(), rb.color.getBlue());
    }


}
