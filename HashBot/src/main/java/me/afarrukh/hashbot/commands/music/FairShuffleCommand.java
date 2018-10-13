package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class FairShuffleCommand extends Command implements MusicCommand {

    public FairShuffleCommand() {
        super("fairshuffle");
        addAlias("fs");
        description = "Shuffles the songs and then interleaves them so all users get fair playback.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(MusicUtils.canInteract(evt)) {
            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().fairShuffle();
            evt.getChannel().sendMessage("Shuffled the playlist fairly :ok_hand:").queue();
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
