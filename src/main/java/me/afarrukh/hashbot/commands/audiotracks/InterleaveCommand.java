package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InterleaveCommand extends Command implements AudioTrackCommand {

    private final AudioTrackManager audioTrackManager;

    public InterleaveCommand(AudioTrackManager audioTrackManager) {
        super("interleave");
        this.audioTrackManager = audioTrackManager;
        addAlias("il");
        description = "Interleaves the tracks in the queue so that all users get fair playback.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (AudioTrackUtils.canInteract(evt)) {
            audioTrackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().interleave(false);
            evt.getChannel().sendMessage("Interleaved the playlist :ok_hand:").queue();
        }
    }
}
