package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.track.TrackScheduler;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LoopCommand extends Command implements AudioTrackCommand {

    public LoopCommand() {
        super("loop");
        description = "Loops the currently playing track. Will no longer loop if next track is skipped to";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (AudioTrackUtils.canInteract(evt)) {
            TrackScheduler trackScheduler =
                    Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler();
            trackScheduler.setLooping(!trackScheduler.isLooping());
            String status;
            if (trackScheduler.isLooping()) status = "Now";
            else status = "No longer";
            evt.getChannel()
                    .sendMessage(status + " looping: `"
                            + Bot.trackManager
                                    .getGuildAudioPlayer(evt.getGuild())
                                    .getPlayer()
                                    .getPlayingTrack()
                                    .getInfo()
                                    .title
                            + "`")
                    .queue();
        }
    }
}
