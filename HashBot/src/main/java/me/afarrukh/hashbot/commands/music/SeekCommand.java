package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SeekCommand extends Command {

    public SeekCommand() {
        super("seek", new String[]{"skim"});
        description = "Seeks to the particular time (in seconds) of the currently playing song.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(MusicUtils.canInteract(evt)) {
            try {
                String[] tokens = params.split( " ");
                int seconds = Integer.parseInt(tokens[0]);
                MusicUtils.seek(evt, seconds);
            } catch(NumberFormatException e) {evt.getChannel().sendMessage("Need to specify how many seconds to seek.").queue();
            } catch(NullPointerException e) {evt.getChannel().sendMessage("Usage: seek/skim <number of seconds into current song>").queue();}
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
