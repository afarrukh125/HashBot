package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class InterleaveCommand extends Command implements MusicCommand {

    public InterleaveCommand() {
        super("interleave");
        addAlias("il");
        description = "Interleaves the tracks in the queue so that all users get fair playback.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (MusicUtils.canInteract(evt)) {
            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().interleave(false);
            evt.getChannel().sendMessage("Interleaved the playlist :ok_hand:").queue();
        }
    }
}
