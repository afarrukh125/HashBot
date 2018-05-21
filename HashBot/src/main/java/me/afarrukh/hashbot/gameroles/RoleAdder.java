package me.afarrukh.hashbot.gameroles;

import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Timer;
import java.util.TimerTask;

public class RoleAdder {
    public User user;

    private Message message;
    public Guild guild;
    private Timer timeoutTimer;

    private int stage = 0;
    private int page = 1;

    private final String e_back = "↩";
    private final String cancel = "\u26D4";
    private final String e_one = "1⃣";
    private final String e_two = "2⃣";
    private final String e_three = "3⃣";
    private final String e_four = "4⃣";
    private final String e_five = "5⃣";
    private final String e_six = "6⃣";
    private final String e_seven = "7⃣";
    private final String e_eight = "8⃣";
    private final String e_nine = "9⃣";
    private final String e_ten = "\uD83D\uDD1F";
    private final String e_left = "\u25C0";
    private final String e_right = "\u25B6";
    private final String confirm = "\u2705";

    public RoleAdder(MessageReceivedEvent evt) {
        this.guild = evt.getGuild();
        this.user = evt.getAuthor();

        timeoutTimer = new Timer();
        timeoutTimer.schedule(new RoleAdder.InactiveTimer(this, evt.getGuild()),30*1000); //30 second timer before builder stops

        message = evt.getChannel().sendMessage(EmbedUtils.getGameRoleListEmbed(this, 1)).complete();

        message.addReaction(e_back).queue();
        message.addReaction(e_left).queue();
        message.addReaction(cancel).queue();
        message.addReaction(e_one).queue();
        message.addReaction(e_two).queue();
        message.addReaction(e_three).queue();
        message.addReaction(e_four).queue();
        message.addReaction(e_five).queue();
        message.addReaction(e_six).queue();
        message.addReaction(e_seven).queue();
        message.addReaction(e_eight).queue();
        message.addReaction(e_nine).queue();
        message.addReaction(e_ten).queue();
        message.addReaction(e_right).queue();
        message.addReaction(confirm).queue();

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

    }

    private void confirmRole(GuildMessageReactionAddEvent evt) {

    }

    private void endSession() {
        Bot.gameRoleManager.getGuildRoleManager(guild).getRoleAdders().remove(this);
        message.delete().queue();
        this.timeoutTimer.cancel();
    }

    private class InactiveTimer extends TimerTask {
        private RoleAdder adder;
        private Guild guild;
        private InactiveTimer(RoleAdder dder, Guild guild) {
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
