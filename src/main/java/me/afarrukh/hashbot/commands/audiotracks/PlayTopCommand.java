package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import me.afarrukh.hashbot.track.results.YTLinkResultHandler;
import me.afarrukh.hashbot.track.results.YTSearchResultHandler;
import net.dv8tion.jda.api.JDA;
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

        if (isBotConnected(evt)) {
            return;
        }

        var injector = Guice.createInjector(new CoreBotModule());
        AudioTrackManager trackManager = injector.getInstance(AudioTrackManager.class);
        GuildAudioTrackManager gmm = trackManager.getGuildAudioPlayer(evt.getGuild());
        if (params.split(" ").length == 1 && params.contains("http")) {
            trackManager.getPlayerManager().loadItemOrdered(gmm, params, new YTLinkResultHandler(gmm, evt, true));
            evt.getMessage().delete().queue();
        } else {
            trackManager.getPlayerManager().loadItem("ytsearch: " + params, new YTSearchResultHandler(gmm, evt, true));
        }
    }

    static boolean isBotConnected(MessageReceivedEvent evt) {
        var injector = Guice.createInjector(new CoreBotModule());
        JDA botUser = injector.getInstance(JDA.class);
        if (evt.getGuild()
                        .getMemberById(botUser.getSelfUser().getId())
                        .getVoiceState()
                        .getChannel()
                != null) { // If the bot is already connected
            if (!evt.getGuild()
                    .getMemberById(botUser.getSelfUser().getId())
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
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: play <youtube search> OR play <youtube link>")
                .queue();
    }
}
