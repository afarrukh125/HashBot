package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ShuffleCommand extends Command {

    public ShuffleCommand() {
        super("shuffle");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(MusicUtils.canInteract(evt)) {
            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().shuffle();
            evt.getChannel().sendMessage("Shuffled the playlist :ok_hand:").queue();
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
