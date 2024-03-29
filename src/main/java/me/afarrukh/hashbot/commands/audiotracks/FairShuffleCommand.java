package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class FairShuffleCommand extends Command implements AudioTrackCommand {

    public FairShuffleCommand() {
        super("fairshuffle");
        description = "Shuffles the tracks and then interleaves them so all users get fair playback.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!AudioTrackUtils.canInteract(evt)) return;

        Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().fairShuffle();
        evt.getChannel().sendMessage("Shuffled the playlist fairly :ok_hand:").queue();
    }
}
