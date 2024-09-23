package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.track.TrackScheduler;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class FairPlayCommand extends Command implements AudioTrackCommand {

    private final AudioTrackManager audioTrackManager;

    public FairPlayCommand(AudioTrackManager audioTrackManager) {
        super("fairplay");
        this.audioTrackManager = audioTrackManager;
        addAlias("fp");
        description =
                "If this is turned on, tracks are automatically queued and sorted so everyone gets an equal chance to queue.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!AudioTrackUtils.canInteract(evt)) return;

        TrackScheduler ts =
                audioTrackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler();

        if (ts.isLoopingQueue()) {
            evt.getChannel()
                    .sendMessage("Cannot use this feature unless looping queue is disabled.")
                    .queue();
            return;
        }

        ts.setFairPlay(!ts.isFairPlay());

        StringBuilder sb = new StringBuilder();
        sb.append("Fairplay mode is now ");
        final String onOrOff = ts.isFairPlay() ? "on" : "off";
        sb.append(onOrOff).append(".");
        evt.getChannel().sendMessage(sb.toString()).queue();
    }
}
