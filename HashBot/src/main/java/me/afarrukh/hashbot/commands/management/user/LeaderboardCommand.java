package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.utils.EmbedUtils;
import me.afarrukh.hashbot.utils.LevelUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class LeaderboardCommand extends Command {

    public LeaderboardCommand() {
        super("leaderboard");
        addAlias("lb");
        description = "Shows the leaderboard for this server. You can provide 'credits' as a parameter to see the credits leaderboard, instead";
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {

        if (params != null) {
            if (params.equalsIgnoreCase("credits")) {
                evt.getChannel().sendMessage(EmbedUtils.getCreditsLeaderboardEmbed(LevelUtils.getCreditsLeaderboard(evt.getGuild()), evt)).queue();
            }
        } else {
            Member[] userList = LevelUtils.getLeaderboard(evt.getGuild());
            evt.getChannel().sendMessage(EmbedUtils.getLeaderboard(userList, evt)).queue();
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
