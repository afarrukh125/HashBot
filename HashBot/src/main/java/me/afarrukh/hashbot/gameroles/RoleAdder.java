package me.afarrukh.hashbot.gameroles;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.BotUtils;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RoleAdder {
    public User user;

    public GameRole desiredRole = null;

    private Message message;
    public Guild guild;
    private Timer timeoutTimer;

    private int stage = 0;
    private int page = 1;
    private int maxPageNumber;

    private final String back = "↩";
    private final String cancel = "\u26D4";
    private final String[] numberEmojis;
    private final String e_left = "\u25C0";
    private final String e_right = "\u25B6";
    private final String confirm = "\u2705";

    public RoleAdder(MessageReceivedEvent evt) {
        this.guild = evt.getGuild();
        this.user = evt.getAuthor();
        numberEmojis = BotUtils.createNumberEmojiArray();

        timeoutTimer = new Timer();
        timeoutTimer.schedule(new RoleAdder.InactiveTimer(this, evt.getGuild()),30*1000); //30 second timer before builder stops

        message = evt.getChannel().sendMessage(EmbedUtils.getGameRoleListEmbed(this, page)).complete();

        message.addReaction(back).queue();
        message.addReaction(e_left).queue();
        message.addReaction(cancel).queue();
        for(int i = 0; i<BotUtils.getMaxEntriesOnPage(this, page); i++) {
            message.addReaction(numberEmojis[i]).queue();
        }
        message.addReaction(e_right).queue();

        List<GameRole> roleList = Bot.gameRoleManager.getGuildRoleManager(guild).getGameRoles();

        int maxPageNumber = roleList.size()/10+1; //We need to know how many songs are displayed per page

        //This block of code is to prevent the list from displaying a blank page as the last one
        if(roleList.size()%10 == 0)
            maxPageNumber--;

        this.maxPageNumber = maxPageNumber;

        Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getRoleAdders().add(this);
    }

    public void handleEvent(GuildMessageReactionAddEvent evt) {
        timeoutTimer.cancel();
        timeoutTimer = new Timer();
        timeoutTimer.schedule(new RoleAdder.InactiveTimer(this, guild),30*1000);

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
        switch(reactionName) {
            case cancel:
                endSession();
                return;
            case back:
                return;
            case e_left:
                if(page <= 1)
                    return;
                page--;
                message.clearReactions().queue();
                message.addReaction(back).queue();
                message.addReaction(e_left).queue();
                message.addReaction(cancel).queue();
                for(int i = 0; i<BotUtils.getMaxEntriesOnPage(this, page); i++) {
                    message.addReaction(numberEmojis[i]).queue();
                }
                message.addReaction(e_right).queue();
                message.editMessage(EmbedUtils.getGameRoleListEmbed(this, page)).queue();
                return;
            case e_right:
                if(page >= maxPageNumber)
                    return;
                page++;
                message.clearReactions().queue();
                message.addReaction(back).queue();
                message.addReaction(e_left).queue();
                message.addReaction(cancel).queue();
                for(int i = 0; i<BotUtils.getMaxEntriesOnPage(this, page); i++) {
                    message.addReaction(numberEmojis[i]).queue();
                }
                message.addReaction(e_right).queue();
                message.editMessage(EmbedUtils.getGameRoleListEmbed(this, page)).queue();
                return;
            case confirm:
                return;
            case "\uD83D\uDD1F":
                desiredRole = Bot.gameRoleManager.getGuildRoleManager(guild).getGameRoles().get((10*page)-1);
                break;
            default:
                int index = Integer.parseInt(Character.toString(reactionName.charAt(0)));
                desiredRole = Bot.gameRoleManager.getGuildRoleManager(guild).getGameRoles()
                        .get(((page-1)*10)+(index-1));
                break;
        }
        stage++;
        message.clearReactions().queue();
        message.addReaction(back).queue();
        message.addReaction(cancel).queue();
        message.addReaction(confirm).queue();
        message.editMessage(EmbedUtils.confirmDesiredRole(this)).queue();
    }

    private void confirmRole(GuildMessageReactionAddEvent evt) {
        String reactionName = evt.getReaction().getReactionEmote().getName();
        switch(reactionName) {
            case cancel:
                endSession();
                return;
            case back:
                stage--;
                message.clearReactions().queue();
                message.addReaction(back).queue();
                message.addReaction(e_left).queue();
                message.addReaction(cancel).queue();
                for(int i = 0; i<BotUtils.getMaxEntriesOnPage(this, page); i++) {
                    message.addReaction(numberEmojis[i]).queue();
                }
                message.addReaction(e_right).queue();
                message.editMessage(EmbedUtils.getGameRoleListEmbed(this, page)).queue();
                return;
            case confirm:
                Bot.gameRoleManager.getGuildRoleManager(guild).getRoleBuilders().remove(this);
                if(desiredRole == null) {
                    message.editMessage(EmbedUtils.getNullRoleEmbed(this)).queue();
                    return;
                }

                if(guild.getMemberById(user.getId()).getRoles()
                        .contains(Bot.gameRoleManager.getGuildRoleManager(guild).getRoleFromGameRole(desiredRole))) {
                    message.editMessage(EmbedUtils.alreadyHasRoleEmbed(this)).queue();
                    return;
                }
                message.editMessage(EmbedUtils.addRoleCompleteEmbed(this)).queue();
                BotUtils.addRoleToMember(this);
                return;

        }
    }

    private void endSession() {
        Bot.gameRoleManager.getGuildRoleManager(guild).getRoleAdders().remove(this);
        message.delete().queue();
        this.timeoutTimer.cancel();
    }

    private class InactiveTimer extends TimerTask {
        private RoleAdder adder;
        private Guild guild;
        private InactiveTimer(RoleAdder adder, Guild guild) {
            this.adder = adder;
            this.guild = guild;
        }
        @Override
        public void run() {
            Bot.gameRoleManager.getGuildRoleManager(guild).getRoleAdders().remove(adder);
            message.delete().queue();
            adder.timeoutTimer.cancel();
        }

    }
}
