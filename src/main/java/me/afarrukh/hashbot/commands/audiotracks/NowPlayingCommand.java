package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NowPlayingCommand extends Command implements AudioTrackCommand {
    public NowPlayingCommand(Database database) {
        super("nowplaying", database);
        addAlias("current");
        addAlias("np");
        description = "Shows the currently playing track";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        try {
            var injector = Guice.createInjector(new CoreBotModule());
            AudioTrack currentTrack = injector.getInstance(AudioTrackManager.class)
                    .getGuildAudioPlayer(evt.getGuild())
                    .getPlayer()
                    .getPlayingTrack();
            evt.getChannel()
                    .sendMessageEmbeds(EmbedUtils.getSingleTrackEmbed(currentTrack, evt))
                    .queue();
        } catch (NullPointerException e) {
            evt.getChannel()
                    .sendMessageEmbeds(EmbedUtils.getNothingPlayingEmbed())
                    .queue();
        }
    }
}
