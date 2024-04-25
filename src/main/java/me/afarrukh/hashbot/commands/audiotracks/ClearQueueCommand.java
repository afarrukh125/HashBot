package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ClearQueueCommand extends Command implements AudioTrackCommand {

    private final AudioTrackManager audioTrackManager;

    public ClearQueueCommand(AudioTrackManager audioTrackManager) {
        super("clearqueue");
        this.audioTrackManager = audioTrackManager;
        addAlias("cq");
        description = "Clears the current queue for the track player";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!AudioTrackUtils.canInteract(evt)) return;

        if (audioTrackManager
                .getGuildAudioPlayer(evt.getGuild())
                .getScheduler()
                .getQueue()
                .isEmpty()) {
            evt.getChannel().sendMessage("Queue is empty - nothing cleared").queue();
            return;
        }
        audioTrackManager
                .getGuildAudioPlayer(evt.getGuild())
                .getScheduler()
                .getQueue()
                .clear();
        evt.getChannel().sendMessage("Queue cleared :ok_hand:").queue();
    }
}
