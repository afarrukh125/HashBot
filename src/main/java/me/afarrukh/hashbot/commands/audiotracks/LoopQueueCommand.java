package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.track.TrackScheduler;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LoopQueueCommand extends Command implements AudioTrackCommand {

    public LoopQueueCommand() {
        super("loopqueue");
        addAlias("lq");
        description = "Adds tracks to the end of the queue once they are complete.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (AudioTrackUtils.canInteract(evt)) {
            TrackScheduler ts =
                    Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler();
            if (ts.isFairPlay()) {
                evt.getChannel()
                        .sendMessage("Cannot use this feature unless fairplay is disabled.")
                        .queue();
                return;
            }
            ts.setLoopingQueue(!ts.isLoopingQueue());
            String string = (ts.isLoopingQueue()
                    ? "Now looping the queue. Tracks will be added" + " to the back of the queue."
                    : "No longer looping the queue.");
            evt.getChannel().sendMessage(string).queue();
        }
    }
}
