package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PauseCommand extends Command {

    public PauseCommand() {
        super("pause");
        description = "Pauses the music player";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(MusicUtils.canInteract(evt)) {
            MusicUtils.pause(evt);
            String pau = "";
            if(Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().isPaused())
                pau = "Paused";
            else
                pau = "Unpaused";
            evt.getChannel().sendMessage(pau).queue();
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
