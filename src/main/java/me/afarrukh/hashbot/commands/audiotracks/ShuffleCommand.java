package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ShuffleCommand extends Command implements AudioTrackCommand {

    private final AudioTrackManager audioTrackManager;

    public ShuffleCommand(AudioTrackManager audioTrackManager) {
        super("shuffle");
        this.audioTrackManager = audioTrackManager;
        description = "Shuffles the track queue";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (AudioTrackUtils.canInteract(evt)) {
            audioTrackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().shuffleAndReplace();
            evt.getChannel().sendMessage("Shuffled the playlist :ok_hand:").queue();
        }
    }
}
