package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class FairShuffleCommand extends Command implements AudioTrackCommand {

    private final AudioTrackManager audioTrackManager;

    public FairShuffleCommand(AudioTrackManager audioTrackManager) {
        super("fairshuffle");
        this.audioTrackManager = audioTrackManager;
        description = "Shuffles the tracks and then interleaves them so all users get fair playback.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!AudioTrackUtils.canInteract(evt)) return;

        audioTrackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().fairShuffle();
        evt.getChannel().sendMessage("Shuffled the playlist fairly :ok_hand:").queue();
    }
}
