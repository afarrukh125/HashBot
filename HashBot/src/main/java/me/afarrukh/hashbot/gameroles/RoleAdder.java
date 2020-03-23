package me.afarrukh.hashbot.gameroles;

import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.utils.BotUtils;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RoleAdder implements RoleGUI {
    private final User user;
    private final Message message;
    private final Guild guild;
    private final int maxPageNumber;
    private final List<GameRole> roleList;
    private final String back = "↩";
    private final String cancel = "\u26D4";
    private final String[] numberEmojis = BotUtils.createNumberEmojiArray();
    private final String e_left = "\u25C0";
    private final String e_right = "\u25B6";
    private final String confirm = "\u2705";
    private GameRole desiredRole = null;
    private Timer timeoutTimer;
    private int stage = 0;
    private int page = 1;

    public RoleAdder(GuildMessageReceivedEvent evt) {
        this.guild = evt.getGuild();
        this.user = evt.getAuthor();

        timeoutTimer = new Timer();
        timeoutTimer.schedule(new RoleAdder.InactiveTimer(this, evt.getGuild()), 30 * 1000); //30 second timer before builder stops
        
        this.roleList = Bot.gameRoleManager.getGuildRoleManager(guild).getGameRoles();
        System.out.println(roleList);

        message = evt.getChannel().sendMessage(EmbedUtils.getGameRoleListEmbed(this, page, roleList)).complete();
        message.editMessage("Please wait for all emojis to appear before selecting an option.").queue();

        message.addReaction(back).queue();
        message.addReaction(e_left).queue();
        message.addReaction(cancel).queue();
        for (int i = 0; i < BotUtils.getMaxEntriesOnPage(roleList, page); i++) {
            message.addReaction(numberEmojis[i]).queue();
        }
        message.addReaction(e_right).queue();

        int maxPageNumber = roleList.size() / 10 + 1; //We need to know how many tracks are displayed per page

        //This block of code is to prevent the list from displaying a blank page as the last one
        if (roleList.size() % 10 == 0)
            maxPageNumber--;

        this.maxPageNumber = maxPageNumber;

        Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).addRoleManagerForUser(user, this);
    }

    @SuppressWarnings("Duplicates")
    public void handleEvent(GuildMessageReactionAddEvent evt) {
        timeoutTimer.cancel();
        timeoutTimer = new Timer();
        timeoutTimer.schedule(new RoleAdder.InactiveTimer(this, guild), 30 * 1000);

        switch (stage) {
            case 0:
                chooseRole(evt);
                break;
            case 1:
                confirmRole(evt);
                break;
            default:
                break;
        }
        evt.getReaction().removeReaction(evt.getUser()).complete();
    }

    private void chooseRole(GuildMessageReactionAddEvent evt) {
        String reactionName = evt.getReaction().getReactionEmote().getName();
        switch (reactionName) {
            case cancel:
                endSession();
                return;
            case back:
            case confirm:
                return;
            case e_left:
                if (page <= 1)
                    return;
                page--;
                message.editMessage(EmbedUtils.getGameRoleListEmbed(this, page, roleList)).queue();
                message.clearReactions().complete();
                message.addReaction(back).queue();
                message.addReaction(e_left).queue();
                message.addReaction(cancel).queue();
                for (int i = 0; i < BotUtils.getMaxEntriesOnPage(roleList, page); i++) {
                    message.addReaction(numberEmojis[i]).queue();
                }
                message.addReaction(e_right).queue();
                return;
            case e_right:
                if (page >= maxPageNumber)
                    return;
                page++;
                message.editMessage(EmbedUtils.getGameRoleListEmbed(this, page, roleList)).queue();
                message.clearReactions().complete();
                message.addReaction(back).queue();
                message.addReaction(e_left).queue();
                message.addReaction(cancel).queue();
                for (int i = 0; i < BotUtils.getMaxEntriesOnPage(roleList, page); i++) {
                    message.addReaction(numberEmojis[i]).queue();
                }
                message.addReaction(e_right).queue();
                return;
            case "\uD83D\uDD1F":
                desiredRole = Bot.gameRoleManager.getGuildRoleManager(guild).getGameRoles().get((10 * page) - 1);
                break;
            default:
                try {
                    int index = Integer.parseInt(Character.toString(reactionName.charAt(0)));
                    desiredRole = Bot.gameRoleManager.getGuildRoleManager(guild).getGameRoles()
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
        message.editMessage(EmbedUtils.confirmDesiredRole(this)).queue();
    }

    private void confirmRole(GuildMessageReactionAddEvent evt) {
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
                for (int i = 0; i < BotUtils.getMaxEntriesOnPage(roleList, page); i++) {
                    message.addReaction(numberEmojis[i]).queue();
                }
                message.addReaction(e_right).queue();
                message.editMessage(EmbedUtils.getGameRoleListEmbed(this, page, roleList)).queue();
                return;
            case confirm:
                message.clearReactions().complete();
                Bot.gameRoleManager.getGuildRoleManager(guild).removeRoleManagerForUser(user);
                if (desiredRole == null) {
                    message.editMessage(EmbedUtils.getNullRoleEmbed(this.guild)).queue();
                    return;
                }
                if (guild.getMemberById(user.getId()).getRoles()
                        .contains(Bot.gameRoleManager.getGuildRoleManager(guild).getRoleFromGameRole(desiredRole))) {
                    message.editMessage(EmbedUtils.alreadyHasRoleEmbed(Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getRoleFromGameRole(this.desiredRole))).queue();
                    return;
                }
                try {
                    message.editMessage(EmbedUtils.addRoleCompleteEmbed(Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getRoleFromGameRole(this.desiredRole))).queue();
                    BotUtils.addRoleToMember(this);
                } catch (IllegalArgumentException e) {
                    message.editMessage(EmbedUtils.getNullRoleEmbed(this.guild)).queue();
                    GuildDataManager jgm = GuildDataMapper.getInstance().getDataManager(evt.getGuild());
                    jgm.removeRole(desiredRole.getName());
                }
                return;
            default:
                break;
        }
    }

    public void endSession() {
        Bot.gameRoleManager.getGuildRoleManager(guild).removeRoleManagerForUser(user);
        this.timeoutTimer.cancel();
        message.delete().queue();
    }

    @Override
    public User getUser() {
        return user;
    }

    public GameRole getDesiredRole() {
        return desiredRole;
    }

    @Override
    public Guild getGuild() {
        return guild;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    private class InactiveTimer extends TimerTask {
        private final RoleAdder adder;
        private final Guild guild;

        private InactiveTimer(RoleAdder adder, Guild guild) {
            this.adder = adder;
            this.guild = guild;
        }

        @Override
        public void run() {
            Bot.gameRoleManager.getGuildRoleManager(guild).removeRoleManagerForUser(user);
            adder.timeoutTimer.cancel();
            message.delete().queue();
        }

    }
}
