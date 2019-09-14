package me.afarrukh.hashbot.gameroles;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import me.afarrukh.hashbot.utils.BotUtils;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RoleDeleter implements RoleGUI {

    private final Guild guild;
    private final User user;
    private final Message message;
    private final int maxPageNumber;
    private final ArrayList<GameRole> createdRoles;
    private final String back = "↩";
    private final String cancel = "\u26D4";
    private final String[] numberEmojis;
    private final String e_left = "\u25C0";
    private final String e_right = "\u25B6";
    private final String confirm = "\u2705";
    private GameRole roleToBeDeleted = null;
    private int stage = 0;
    private int page = 1;
    private Timer timeoutTimer;

    public RoleDeleter(MessageReceivedEvent evt) {
        this.guild = evt.getGuild();
        this.user = evt.getAuthor();
        this.numberEmojis = BotUtils.createNumberEmojiArray();

        timeoutTimer = new Timer();
        timeoutTimer.schedule(new RoleDeleter.InactiveTimer(this, evt.getGuild()), 30 * 1000); //30 second timer before builder stops

        this.createdRoles = new ArrayList<>();

        for (GameRole gr : Bot.gameRoleManager.getGuildRoleManager(getGuild()).getGameRoles()) {
            if (gr.getCreator().equals(getUser().getId()))
                createdRoles.add(gr);
        }

        message = evt.getChannel().sendMessage(EmbedUtils.getCreatedRolesEmbed(this, page, createdRoles)).complete();
        message.editMessage("Please wait for all emojis to appear before selecting an option.").queue();

        message.addReaction(back).queue();
        message.addReaction(e_left).queue();
        message.addReaction(cancel).queue();
        for (int i = 0; i < BotUtils.getMaxEntriesOnPage(createdRoles, page); i++) {
            message.addReaction(numberEmojis[i]).queue();
        }
        message.addReaction(e_right).queue();

        List<GameRole> roleList = Bot.gameRoleManager.getGuildRoleManager(guild).getGameRoles();

        int maxPageNumber = roleList.size() / 10 + 1; //We need to know how many songs are displayed per page

        //This block of code is to prevent the list from displaying a blank page as the last one
        if (roleList.size() % 10 == 0)
            maxPageNumber--;

        this.maxPageNumber = maxPageNumber;

        Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getRoleModifiers().add(this);

    }

    @Override
    public void handleEvent(GuildMessageReactionAddEvent evt) {
        timeoutTimer.cancel();
        timeoutTimer = new Timer();
        timeoutTimer.schedule(new RoleDeleter.InactiveTimer(this, guild), 30 * 1000);

        switch (stage) {
            case 0:
                chooseRole(evt);
                break;
            case 1:
                confirmDeletion(evt);
                break;
            default:
                break;
        }
        evt.getReaction().removeReaction(evt.getUser()).complete();
    }

    private void chooseRole(GuildMessageReactionAddEvent evt) {
        String reactionString = evt.getReaction().getReactionEmote().getName();
        switch (reactionString) {
            case cancel:
                endSession();
                return;
            case back:
                return;
            case e_left:
                if (page <= 1)
                    return;
                page--;
                message.editMessage(EmbedUtils.getCreatedRolesEmbed(this, page, createdRoles)).queue();
                message.clearReactions().complete();
                message.addReaction(back).queue();
                message.addReaction(e_left).queue();
                message.addReaction(cancel).queue();
                for (int i = 0; i < BotUtils.getMaxEntriesOnPage(createdRoles, page); i++) {
                    message.addReaction(numberEmojis[i]).queue();
                }
                message.addReaction(e_right).queue();
                return;
            case e_right:
                if (page >= maxPageNumber)
                    return;
                page++;
                message.editMessage(EmbedUtils.getCreatedRolesEmbed(this, page, createdRoles)).queue();
                message.clearReactions().complete();
                message.addReaction(back).queue();
                message.addReaction(e_left).queue();
                message.addReaction(cancel).queue();
                for (int i = 0; i < BotUtils.getMaxEntriesOnPage(createdRoles, page); i++) {
                    message.addReaction(numberEmojis[i]).queue();
                }
                message.addReaction(e_right).queue();
                return;
            case confirm:
                return;
            case "\uD83D\uDD1F":
                roleToBeDeleted = createdRoles.get((10 * page) - 1);
                break;
            default:
                try {
                    int index = Integer.parseInt(Character.toString(reactionString.charAt(0)));
                    roleToBeDeleted = createdRoles
                            .get(((page - 1) * 10) + (index - 1));
                    break;
                } catch (NumberFormatException e) {
                    return;
                }
        }
        stage++;
        message.clearReactions().complete();
        message.addReaction(back).queue();
        message.addReaction(cancel).queue();
        message.addReaction(confirm).queue();
        message.editMessage(EmbedUtils.confirmDeleteRole(this)).queue();
    }

    private void confirmDeletion(GuildMessageReactionAddEvent evt) {
        String reactionName = evt.getReaction().getReactionEmote().getName();
        switch (reactionName) {
            case cancel:
                endSession();
                return;
            case back:
                stage--;
                message.clearReactions().complete();
                message.addReaction(back).queue();
                message.addReaction(e_left).queue();
                message.addReaction(cancel).queue();
                for (int i = 0; i < BotUtils.getMaxEntriesOnPage(createdRoles, page); i++) {
                    message.addReaction(numberEmojis[i]).queue();
                }
                message.addReaction(e_right).queue();
                message.editMessage(EmbedUtils.getCreatedRolesEmbed(this, page, createdRoles)).queue();
                return;
            case confirm:
                message.clearReactions().complete();
                Bot.gameRoleManager.getGuildRoleManager(guild).getRoleModifiers().remove(this);
                if (roleToBeDeleted == null) {
                    message.editMessage(new EmbedBuilder().setColor(Constants.EMB_COL)
                            .appendDescription("Please select a role to delete.").build()).queue();
                    return;
                }
                try {
                    message.editMessage(EmbedUtils.deleteRoleCompleteEmbed(this)).queue();
                    BotUtils.deleteRole(this);
                    Bot.gameRoleManager.getGuildRoleManager(guild).removeRole(roleToBeDeleted.getName());
                } catch (IllegalArgumentException e) {
                    message.editMessage(EmbedUtils.getNullRoleEmbed(this.guild)).queue();
                    GuildDataManager jgm = GuildDataMapper.getInstance().getDataManager(evt.getGuild());
                    jgm.removeRole(roleToBeDeleted.getName());
                }
                return;
            default:
                break;
        }
    }

    private void endSession() {
        Bot.gameRoleManager.getGuildRoleManager(guild).getRoleModifiers().remove(this);
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

    public GameRole getRoleToBeDeleted() {
        return roleToBeDeleted;
    }

    private class InactiveTimer extends TimerTask {

        private final RoleDeleter deleter;
        private final Guild guild;

        InactiveTimer(RoleDeleter deleter, Guild guild) {
            this.deleter = deleter;
            this.guild = guild;
        }

        @Override
        public void run() {
            Bot.gameRoleManager.getGuildRoleManager(guild).getRoleModifiers().remove(deleter);
            message.delete().queue();
            deleter.timeoutTimer.cancel();
        }
    }
}

