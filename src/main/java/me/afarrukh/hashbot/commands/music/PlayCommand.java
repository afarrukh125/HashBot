package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.music.GuildMusicManager;
import me.afarrukh.hashbot.music.results.YTLinkResultHandler;
import me.afarrukh.hashbot.music.results.YTSearchResultHandler;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;

public class PlayCommand extends Command implements MusicCommand {

    public PlayCommand() {
        super("play");
        addAlias("p");
        description = "Adds a track to the music queue. If you are queuing a playlist, you can provide 'shuffle' as an additional parameter " +
                "to shuffle the list before adding it";
        addParameter("track name, track link, or youtube playlist link", "The name/link of the youtube track, or playlist to be queued");
        addExampleUsage("p name");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (params == null) {
            onIncorrectParams(evt.getTextChannel());
            return;
        }

        if (evt.getMember() != null && Objects.requireNonNull(evt.getMember().getVoiceState()).getChannel() == null)
            return;

        if (PlayTopCommand.isBotConnected(evt)) return;

        GuildMusicManager gmm = Bot.musicManager.getGuildAudioPlayer(evt.getGuild());
        // To account for shuffling the list, we have the first branch
        if (params.split(" ").length == 2 && params.contains("http") && params.contains("shuffle")) {
            Bot.musicManager.getPlayerManager().loadItemOrdered(gmm, params.split(" ")[0], new YTLinkResultHandler(gmm, evt, false));
            evt.getMessage().delete().queue();
        } else if (params.split(" ").length == 1 && params.contains("http")) {
            Bot.musicManager.getPlayerManager().loadItemOrdered(gmm, params, new YTLinkResultHandler(gmm, evt, false));
            evt.getMessage().delete().queue();
        } else
            Bot.musicManager.getPlayerManager().loadItem("ytsearch: " + params, new YTSearchResultHandler(gmm, evt, false));
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: play <youtube search> OR play <youtube link>").queue();
    }
}
