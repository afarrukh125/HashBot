package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SeekCommand extends Command implements MusicCommand {

    public SeekCommand() {
        super("seek", new String[]{"skim", "ff"});
        description = "Seeks to the particular time (in seconds) of the currently playing song.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(MusicUtils.canInteract(evt)) {
            try {
                if(params.split("\\.").length == 2) {
                    String[] tokens = params.split("\\.");
                    int minute = Integer.parseInt(tokens[0]);
                    int seconds = Integer.parseInt(tokens[1]);

                    int desiredMinute = minute*60;
                    int desiredSeconds = seconds%60;

                    if((tokens[1]).length() == 1) {
                        desiredSeconds *= 6;
                    }

                    int desiredTime = desiredMinute + desiredSeconds;
                    MusicUtils.seek(evt, desiredTime);
                }
                else {
                    String[] tokens = params.split(" ");
                    int seconds = Integer.parseInt(tokens[0]);
                    MusicUtils.seek(evt, seconds);
                }
            } catch(NumberFormatException e) {
                    evt.getChannel().sendMessage("Need to specify a valid number of seconds to seek.").queue();
            } catch(NullPointerException e) {evt.getChannel().sendMessage("Usage: seek/skim <number of seconds into current song>").queue();}
              catch(ArrayIndexOutOfBoundsException e) { evt.getTextChannel().sendMessage("Please enter a valid number of seconds to skip.").queue();}
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
