package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import me.afarrukh.hashbot.track.results.YTLinkResultHandler;
import me.afarrukh.hashbot.track.results.YTSearchResultHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PlayTopCommand extends Command implements AudioTrackCommand {

    private final Database database;
    private final JDA jda;
    private final AudioTrackManager audioTrackManager;

    public PlayTopCommand(Database database, JDA jda, AudioTrackManager audioTrackManager) {
        super("playtop");
        this.database = database;
        this.jda = jda;
        this.audioTrackManager = audioTrackManager;
        addAlias("ptop");
        description = "Adds a track to the top of the track queue";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (params == null) {
            onIncorrectParams(database, evt.getChannel().asTextChannel());
            return;
        }

        if (isBotConnected(jda, evt)) {
            return;
        }

        GuildAudioTrackManager gmm = audioTrackManager.getGuildAudioPlayer(evt.getGuild());
        if (params.split(" ").length == 1 && params.contains("http")) {
            audioTrackManager.getPlayerManager().loadItemOrdered(gmm, params, new YTLinkResultHandler(gmm, evt, true, database));
            evt.getMessage().delete().queue();
        } else {
            audioTrackManager
                    .getPlayerManager()
                    .loadItem("ytsearch: " + params, new YTSearchResultHandler(gmm, evt, true, database));
        }
    }

    static boolean isBotConnected(JDA jda, MessageReceivedEvent evt) {
        if (evt.getGuild()
                        .getMemberById(jda.getSelfUser().getId())
                        .getVoiceState()
                        .getChannel()
                != null) { // If the bot is already connected
            if (!evt.getGuild()
                    .getMemberById(jda.getSelfUser().getId())
                    .getVoiceState()
                    .getChannel()
                    .equals(evt.getMember().getVoiceState().getChannel())) {
                // If the bot is not in the same channel as the user (assuming already connected) then return
                evt.getChannel()
                        .sendMessage("You must be in the same channel as the bot to queue tracks to it.")
                        .queue();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onIncorrectParams(Database database,  TextChannel channel) {
        channel.sendMessage("Usage: play <youtube search> OR play <youtube link>")
                .queue();
    }
}
