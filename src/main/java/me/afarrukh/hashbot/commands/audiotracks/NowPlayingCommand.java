package me.afarrukh.hashbot.commands.audiotracks;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NowPlayingCommand extends Command implements AudioTrackCommand {
    private final AudioTrackManager audioTrackManager;

    public NowPlayingCommand(AudioTrackManager audioTrackManager) {
        super("nowplaying");
        this.audioTrackManager = audioTrackManager;
        addAlias("current");
        addAlias("np");
        description = "Shows the currently playing track";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        try {
            AudioTrack currentTrack = audioTrackManager
                    .getGuildAudioPlayer(evt.getGuild())
                    .getPlayer()
                    .getPlayingTrack();
            evt.getChannel()
                    .sendMessageEmbeds(EmbedUtils.getSingleTrackEmbed(currentTrack, evt, audioTrackManager))
                    .queue();
        } catch (NullPointerException e) {
            evt.getChannel()
                    .sendMessageEmbeds(EmbedUtils.getNothingPlayingEmbed())
                    .queue();
        }
    }
}
