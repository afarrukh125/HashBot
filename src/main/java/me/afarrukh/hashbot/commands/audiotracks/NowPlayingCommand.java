package me.afarrukh.hashbot.commands.audiotracks;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NowPlayingCommand extends Command implements AudioTrackCommand {
    public NowPlayingCommand() {
        super("nowplaying");
        addAlias("current");
        addAlias("np");
        description = "Shows the currently playing track";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        try {
            AudioTrack currentTrack = Bot.trackManager
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
