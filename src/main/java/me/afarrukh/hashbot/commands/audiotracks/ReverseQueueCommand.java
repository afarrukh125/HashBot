package me.afarrukh.hashbot.commands.audiotracks;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.ArrayList;
import java.util.List;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ReverseQueueCommand extends Command {

    private final AudioTrackManager audioTrackManager;

    public ReverseQueueCommand(AudioTrackManager audioTrackManager) {
        super("reversequeue");
        this.audioTrackManager = audioTrackManager;
        addAlias("rq");
        description = "Inverts the queue contents so that the last track is first and the first track is last. "
                + "Does not affect the currently playing track.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        if (AudioTrackUtils.canInteract(evt)) {

            List<AudioTrack> tracks = audioTrackManager
                    .getGuildAudioPlayer(evt.getGuild())
                    .getScheduler()
                    .getAsArrayList();
            List<AudioTrack> reversedTracks = new ArrayList<>();

            for (int i = tracks.size() - 1; i >= 0; i--) reversedTracks.add(tracks.get(i));

            audioTrackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().replaceQueue(reversedTracks);

            evt.getChannel().sendMessage("The queue has been reversed").queue();
        }
    }
}
