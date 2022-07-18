package me.afarrukh.hashbot.gameroles;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.BotUtils;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RoleBuilder implements RoleGUI {

    public final Message message;
    private final User user;
    private final Guild guild;
    private final String cancel = "\u26D4";
    private final String confirm = "\u2705";
    private final String back = "↩";
    public String roleName;
    private int stage = 0;
    private int red = 255;
    private int green = 255;
    private int blue = 255;
    private Color color;
    private Timer timeoutTimer;

    public RoleBuilder(MessageReceivedEvent evt, String name) {
        this.guild = evt.getGuild();
        this.user = evt.getAuthor();
        TextChannel channel = evt.getTextChannel();

        if (name == null)
            name = " ";

        if (name.split(",").length > 1) {
            String[] tokens = name.split(",");
            this.roleName = tokens[0];

            String colorString = tokens[1].replace('#', ' ').trim();
            try {
                this.red = Integer.valueOf(colorString.substring(0, 2), 16);
                this.green = Integer.valueOf(colorString.substring(2, 4), 16);
                this.blue = Integer.valueOf(colorString.substring(4, 6), 16);

                this.color = hexToRGB(colorString);
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                Random random = new Random();
                this.red = 255;
                this.green = 255;
                this.blue = 255;
                this.color = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
            }
            this.stage = 4;

            message = channel.sendMessageEmbeds(EmbedUtils.getRoleConfirmEmbed(this)).complete();

        } else {
            this.roleName = name;
            message = channel.sendMessageEmbeds(EmbedUtils.getRoleName(name)).complete();
        }

        message.addReaction(back).queue();
        message.addReaction(cancel).queue();
        message.addReaction(confirm).queue();
        message.editMessage("Press the no entry symbol, or do not react for 30 seconds to exit this.\n" +
                "Please type values into the chat and press the green check emoji when you wish to confirm.").queue();

        timeoutTimer = new Timer();
        timeoutTimer.schedule(new RoleBuilder.InactiveTimer(this, evt.getGuild()), 30 * 1000); //30 second timer before builder stops

        Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).addRoleManagerForUser(user, this);
    }

    private static Color hexToRGB(String colorString) {
        return new Color(
                Integer.valueOf(colorString.substring(0, 2), 16),
                Integer.valueOf(colorString.substring(2, 4), 16),
                Integer.valueOf(colorString.substring(4, 6), 16));
    }

    public void handleEvent(MessageReactionAddEvent evt) {
        timeoutTimer.cancel();
        timeoutTimer = new Timer();
        timeoutTimer.schedule(new RoleBuilder.InactiveTimer(this, guild), 30 * 1000);

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

    private void chooseName(MessageReactionAddEvent evt) {
        switch (evt.getReaction().getReactionEmote().getName()) {
            case cancel:
            case back:
                endSession();
                return;
            default:
                break;
        }
        stage++;
        message.editMessageEmbeds(EmbedUtils.getRoleRed(red)).queue();
    }

    private void chooseRed(MessageReactionAddEvent evt) {
        switch (evt.getReaction().getReactionEmote().getName()) {
            case cancel:
                endSession();
                break;
            case back:
                stage--;
                message.editMessageEmbeds(EmbedUtils.getRoleName(roleName)).queue();
                return;
            default:
                break;
        }
        stage++;
        message.editMessageEmbeds(EmbedUtils.getRoleGreen(green)).queue();
    }

    private void chooseGreen(MessageReactionAddEvent evt) {
        switch (evt.getReaction().getReactionEmote().getName()) {
            case cancel:
                endSession();
                break;
            case back:
                stage--;
                message.editMessageEmbeds(EmbedUtils.getRoleRed(red)).queue();
                return;
            default:
                break;
        }
        stage++;
        message.editMessageEmbeds(EmbedUtils.getRoleBlue(blue)).queue();
    }

    private void chooseBlue(MessageReactionAddEvent evt) {
        switch (evt.getReaction().getReactionEmote().getName()) {
            case cancel:
                endSession();
                break;
            case back:
                stage--;
                message.editMessageEmbeds(EmbedUtils.getRoleGreen(green)).queue();
                return;
            default:
                break;
        }
        stage++;
        try {
            color = new Color(red, green, blue);
            message.editMessageEmbeds(EmbedUtils.getRoleConfirmEmbed(this)).queue();
        } catch (IllegalArgumentException e) {
            message.editMessageEmbeds(new EmbedBuilder()
                    .setColor(Constants.EMB_COL)
                    .setTitle("Role builder terminated (Please try again)")
                    .appendDescription(e.getLocalizedMessage()).build())
                    .queue();
            message.clearReactions().complete();
            Bot.gameRoleManager.getGuildRoleManager(guild).removeRoleManagerForUser(user);
        }
    }

    private void confirmRole(MessageReactionAddEvent evt) {

        switch (evt.getReaction().getReactionEmote().getName()) {
            case cancel:
                endSession();
                break;
            case back:
                stage--;
                message.editMessageEmbeds(EmbedUtils.getRoleBlue(blue)).queue();
                break;
            case confirm:
                message.editMessageEmbeds(EmbedUtils.getRoleCompleteEmbed(this)).queue();
                BotUtils.createRole(guild, this);

                Bot.gameRoleManager.getGuildRoleManager(guild).removeRoleManagerForUser(user);
                break;
            default:
        }
    }

    /**
     * What happens when you actually type something to change the group name
     *
     * @param evt The event associated with the message received
     */
    public void handleEvent(MessageReceivedEvent evt) {

        switch (stage) {
            case 0:
                message.editMessageEmbeds(EmbedUtils.getRoleName(evt.getMessage().getContentRaw())).queue();
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
            message.editMessageEmbeds(EmbedUtils.getRoleRed(red)).queue();
        } catch (NumberFormatException e) {
            message.editMessageEmbeds(EmbedUtils.getIllegalColourEmbed()).queue();
        }
    }

    private void chooseGreen(MessageReceivedEvent evt) {
        try {
            green = Integer.parseInt(evt.getMessage().getContentRaw());
            message.editMessageEmbeds(EmbedUtils.getRoleGreen(green)).queue();
        } catch (NumberFormatException e) {
            message.editMessageEmbeds(EmbedUtils.getIllegalColourEmbed()).queue();
        }
    }

    private void chooseBlue(MessageReceivedEvent evt) {
        try {
            blue = Integer.parseInt(evt.getMessage().getContentRaw());
            message.editMessageEmbeds(EmbedUtils.getRoleBlue(blue)).queue();
        } catch (NumberFormatException e) {
            message.editMessageEmbeds(EmbedUtils.getIllegalColourEmbed()).queue();
        }
    }

    public void endSession() {
        Bot.gameRoleManager.getGuildRoleManager(guild).removeRoleManagerForUser(user);
        message.delete().queue();
        this.timeoutTimer.cancel();
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Guild getGuild() {
        return guild;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    public Color getColor() {
        return color;
    }

    private class InactiveTimer extends TimerTask {
        private final RoleBuilder builder;
        private final Guild guild;

        private InactiveTimer(RoleBuilder builder, Guild guild) {
            this.builder = builder;
            this.guild = guild;
        }

        @Override
        public void run() {
            Bot.gameRoleManager.getGuildRoleManager(guild).removeRoleManagerForUser(user);
            message.delete().queue();
            builder.timeoutTimer.cancel();
        }

    }
}