package me.afarrukh.hashbot.gameroles;

import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.JSONGuildManager;
import me.afarrukh.hashbot.utils.BotUtils;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;
import java.awt.font.NumericShaper;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

public class RoleBuilder {

    private User user;
    private Guild guild;
    private int stage = 0;
    public Message message;
    private TextChannel channel;

    public String roleName;
    private int red = 255;
    private int green = 255;
    private int blue = 255;

    private Color color;

    protected Timer timeoutTimer;

    private final String cancel = "\u26D4";
    private final String confirm = "\u2705";
    private final String back = "↩";

    public RoleBuilder(MessageReceivedEvent evt, String name) {
        if(name == null)
            name = " ";
        this.channel = evt.getTextChannel();
        this.guild = evt.getGuild();
        this.user = evt.getAuthor();
        this.roleName = name;
        message = channel.sendMessage(EmbedUtils.getRoleName(name)).complete();
        message.addReaction(back).queue();
        message.addReaction(cancel).queue();
        message.addReaction(confirm).queue();
        message.editMessage("Press the no entry symbol, or do not react for 30 seconds to exit this.\n" +
                "Please type values into the chat and press the green check emoji when you wish to confirm.").queue();

        timeoutTimer = new Timer();
        timeoutTimer.schedule(new RoleBuilder.InactiveTimer(this, evt.getGuild()),30*1000); //30 second timer before builder stops

        Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getRoleBuilders().add(this);
    }

    public void handleEvent(GuildMessageReactionAddEvent evt) {
        timeoutTimer.cancel();
        timeoutTimer = new Timer();
        timeoutTimer.schedule(new RoleBuilder.InactiveTimer(this, guild),30*1000);

        switch (stage) {
            case 0:
                chooseName(evt);
                break;
            case 1:
                chooseRed(evt);
                break;
            case 2:
                chooseGreen(evt);
                break;
            case 3:
                chooseBlue(evt);
                break;
            case 4:
                confirmRole(evt);
                break;
            default:
                break;
        }
        evt.getReaction().removeReaction(evt.getUser()).complete();
    }

    private void chooseName(GuildMessageReactionAddEvent evt) {
        switch(evt.getReaction().getReactionEmote().getName()) {
            case cancel:
                endSession();
                return;
            case back:
                endSession();
                return;
            default:
                break;
        }
        stage++;
        message.editMessage(EmbedUtils.getRoleRed(red)).queue();
    }

    private void chooseRed(GuildMessageReactionAddEvent evt) {
        switch(evt.getReaction().getReactionEmote().getName()) {
            case cancel:
                endSession();
                break;
            case back:
                stage--;
                message.editMessage(EmbedUtils.getRoleName(roleName)).queue();
                return;
            default:
                break;
        }
        stage++;
        message.editMessage(EmbedUtils.getRoleGreen(green)).queue();
    }

    private void chooseGreen(GuildMessageReactionAddEvent evt) {
        switch(evt.getReaction().getReactionEmote().getName()) {
            case cancel:
                endSession();
                break;
            case back:
                stage--;
                message.editMessage(EmbedUtils.getRoleRed(red)).queue();
                return;
            default:
                break;
        }
        stage++;
        message.editMessage(EmbedUtils.getRoleBlue(blue)).queue();
    }

    private void chooseBlue(GuildMessageReactionAddEvent evt) {
        switch(evt.getReaction().getReactionEmote().getName()) {
            case cancel:
                endSession();
                break;
            case back:
                stage--;
                message.editMessage(EmbedUtils.getRoleGreen(green)).queue();
                return;
            default:
                break;
        }
        stage++;
        color = new Color(red, green, blue);
        message.editMessage(EmbedUtils.getRoleConfirmEmbed(this)).queue();
    }

    private void confirmRole(GuildMessageReactionAddEvent evt) {

        switch (evt.getReaction().getReactionEmote().getName()) {
            case cancel:
                endSession();
                break;
            case back:
                stage--;
                message.editMessage(EmbedUtils.getRoleBlue(blue)).queue();
                break;
            case confirm:
                message.editMessage(EmbedUtils.getRoleCompleteEmbed(this)).queue();
                BotUtils.createRole(guild, this);
                Bot.gameRoleManager.getGuildRoleManager(guild).getRoleBuilders().remove(this);
                break;
            default:
                return;
        }
    }

    /**

     * What happens when you actually type something to change the group name
     * @param evt
     */
    public void handleEvent(MessageReceivedEvent evt) {

        switch (stage) {
            case 0:
                message.editMessage(EmbedUtils.getRoleName(evt.getMessage().getContentRaw())).queue();
                roleName = evt.getMessage().getContentRaw();
                break;
            case 1:
                chooseRed(evt);
                break;
            case 2:
                chooseGreen(evt);
                break;
            case 3:
                chooseBlue(evt);
                break;
            default:
                return;
        }

        evt.getMessage().delete().queue();
    }

    private void chooseRed(MessageReceivedEvent evt) {
        try {
            red = Integer.parseInt(evt.getMessage().getContentRaw());
            message.editMessage(EmbedUtils.getRoleRed(red)).queue();
        } catch(NumberFormatException e) {
            message.editMessage(EmbedUtils.getIllegalColourEmbed()).queue();
        }
    }

    private void chooseGreen(MessageReceivedEvent evt) {
        try {
            green = Integer.parseInt(evt.getMessage().getContentRaw());
            message.editMessage(EmbedUtils.getRoleGreen(green)).queue();
        } catch(NumberFormatException e) {
            message.editMessage(EmbedUtils.getIllegalColourEmbed()).queue();
        }
    }

    private void chooseBlue(MessageReceivedEvent evt) {
        try {
            blue = Integer.parseInt(evt.getMessage().getContentRaw());
            message.editMessage(EmbedUtils.getRoleBlue(blue)).queue();
        } catch(NumberFormatException e) {
            message.editMessage(EmbedUtils.getIllegalColourEmbed()).queue();
        }
    }

    private void endSession() {
        Bot.gameRoleManager.getGuildRoleManager(guild).getRoleBuilders().remove(this);
        message.delete().queue();
        this.timeoutTimer.cancel();
    }

    public User getUser() {
        return user;
    }

    public Guild getGuild() {
        return guild;
    }

    public Color getColor() {
        return color;
    }

    private class InactiveTimer extends TimerTask {
        private RoleBuilder builder;
        private Guild guild;
        private InactiveTimer(RoleBuilder builder, Guild guild) {
            this.builder = builder;
            this.guild = guild;
        }
        @Override
        public void run() {
            Bot.gameRoleManager.getGuildRoleManager(guild).getRoleBuilders().remove(builder);
            message.delete().queue();
            builder.timeoutTimer.cancel();
        }

    }
}
