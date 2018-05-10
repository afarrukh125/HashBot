package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.music.GuildMusicManager;
import me.afarrukh.hashbot.music.results.YTLinkResultHandler;
import me.afarrukh.hashbot.music.results.YTSearchResultHandler;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PlayTopCommand extends Command {

    public PlayTopCommand() {
        super("playtop", new String[] {"ptop"});
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        GuildMusicManager gmm = Bot.musicManager.getGuildAudioPlayer(evt.getGuild());
        if(params.split(" ").length == 1 && params.contains("http")) {
            Bot.musicManager.getPlayerManager().loadItemOrdered(gmm, params, new YTLinkResultHandler(gmm, evt, true));
            evt.getMessage().delete();
        }
        else
            Bot.musicManager.getPlayerManager().loadItem("ytsearch: " + params, new YTSearchResultHandler(gmm, evt, true));
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: play <youtube search> OR play <youtube link>").queue();
    }
}
