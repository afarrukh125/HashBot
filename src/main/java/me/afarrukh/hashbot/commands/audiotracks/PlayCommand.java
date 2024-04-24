package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import me.afarrukh.hashbot.track.results.YTLinkResultHandler;
import me.afarrukh.hashbot.track.results.YTSearchResultHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static java.util.Objects.requireNonNull;

public class PlayCommand extends Command implements AudioTrackCommand {

    private final Database database;
    private final JDA jda;

    public PlayCommand(Database database, JDA jda) {
        super("play");
        this.database = database;
        this.jda = jda;
        addAlias("p");
        description =
                "Adds a track to the track queue. If you are queuing a playlist, you can provide 'shuffle' as an additional parameter "
                        + "to shuffle the list before adding it";
        addParameter(
                "track name, track link, or youtube playlist link",
                "The name/link of the youtube track, or playlist to be queued");
        addExampleUsage("p name");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (params == null) {
            onIncorrectParams(database, evt.getChannel().asTextChannel());
            return;
        }

        if (evt.getMember() != null
                && requireNonNull(evt.getMember().getVoiceState()).getChannel() == null) {
            evt.getChannel()
                    .sendMessage("You are not currently connected to voice")
                    .queue();
            return;
        }

        if (PlayTopCommand.isBotConnected(jda, evt)) {
            return;
        }

        GuildAudioTrackManager gmm = Bot.trackManager.getGuildAudioPlayer(evt.getGuild());
        // To account for shuffling the list, we have the first branch
        if (params.split(" ").length == 2 && params.contains("http") && params.contains("shuffle")) {
            Bot.trackManager
                    .getPlayerManager()
                    .loadItemOrdered(gmm, params.split(" ")[0], new YTLinkResultHandler(gmm, evt, false, database));
            evt.getMessage().delete().queue();
        } else if (params.split(" ").length == 1 && params.contains("http")) {
            Bot.trackManager.getPlayerManager().loadItemOrdered(gmm, params, new YTLinkResultHandler(gmm, evt, false, database));
            evt.getMessage().delete().queue();
        } else
            Bot.trackManager
                    .getPlayerManager()
                    .loadItem("ytsearch: " + params, new YTSearchResultHandler(gmm, evt, false, database));
    }

    @Override
    public void onIncorrectParams(Database database, TextChannel channel) {
        channel.sendMessage("Usage: play <youtube search> OR play <youtube link>")
                .queue();
    }
}
