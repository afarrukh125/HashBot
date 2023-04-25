package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import me.afarrukh.hashbot.track.results.YTLinkResultHandler;
import me.afarrukh.hashbot.track.results.YTSearchResultHandler;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PlayTopCommand extends Command implements AudioTrackCommand {

    public PlayTopCommand() {
        super("playtop");
        addAlias("ptop");
        description = "Adds a track to the top of the track queue";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (params == null) {
            onIncorrectParams(evt.getChannel().asTextChannel());
            return;
        }

        if (isBotConnected(evt)) return;

        if (Invoker.of(evt.getMember()).getCredit() < Constants.PLAY_TOP_COST) {
            evt.getChannel().sendMessage("You need at least " + Constants.PLAY_TOP_COST + " credit to queue tracks to the top of the list.").queue();
            return;
        }
        GuildAudioTrackManager gmm = Bot.trackManager.getGuildAudioPlayer(evt.getGuild());
        if (params.split(" ").length == 1 && params.contains("http")) {
            Bot.trackManager.getPlayerManager().loadItemOrdered(gmm, params, new YTLinkResultHandler(gmm, evt, true));
            evt.getMessage().delete().queue();
        } else
            Bot.trackManager.getPlayerManager().loadItem("ytsearch: " + params, new YTSearchResultHandler(gmm, evt, true));
    }

    static boolean isBotConnected(MessageReceivedEvent evt) {
        if (evt.getGuild().getMemberById(Bot.botUser().getSelfUser().getId()).getVoiceState().getChannel() != null) { // If the bot is already connected
            if (!evt.getGuild().getMemberById(Bot.botUser().getSelfUser().getId()).getVoiceState().getChannel().equals(evt.getMember().getVoiceState().getChannel())) {
                // If the bot is not in the same channel as the user (assuming already connected) then return
                evt.getChannel().sendMessage("You must be in the same channel as the bot to queue tracks to it.").queue();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: play <youtube search> OR play <youtube link>").queue();
    }
}
