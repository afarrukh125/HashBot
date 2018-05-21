package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.utils.EmbedUtils;
import me.afarrukh.hashbot.utils.LevelUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class LeaderboardCommand extends Command {

    public LeaderboardCommand() {
        super("leaderboard", new String[] {"lb"});
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        Member[] userList = LevelUtils.getLeaderboard(evt.getGuild());

        evt.getChannel().sendMessage(EmbedUtils.getLeaderboard(userList, evt)).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
