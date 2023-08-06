package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SeekCommand extends Command implements AudioTrackCommand {

    public SeekCommand(Database database) {
        super("seek", database);
        addAlias("skim");
        addAlias("ff");
        description = "Seeks to the particular time (in seconds) of the currently playing track.";
        addParameter(
                "position",
                "The position, in either seconds or MM:SS format to seek to in the currently " + "playing track");
        addExampleUsage("seek 1:20");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (AudioTrackUtils.canInteract(evt)) {
            try {
                if (params.split("\\.").length == 2) {
                    String[] tokens = params.split("\\.");
                    toMinute(evt, tokens);
                } else if (params.split(":").length == 2) {
                    String[] tokens = params.split(":");
                    toMinute(evt, tokens);
                } else {
                    String[] tokens = params.split(" ");
                    int seconds = Integer.parseInt(tokens[0]);
                    AudioTrackUtils.seek(evt, seconds);
                }
            } catch (NumberFormatException e) {
                evt.getChannel()
                        .sendMessage("Need to specify a valid number of seconds to seek.")
                        .queue();
            } catch (NullPointerException e) {
                evt.getChannel()
                        .sendMessage("Usage: seek/skim <number of seconds into current track>")
                        .queue();
            } catch (ArrayIndexOutOfBoundsException e) {
                evt.getChannel()
                        .sendMessage("Please enter a valid number of seconds to skip.")
                        .queue();
            }
        }
    }

    private void toMinute(MessageReceivedEvent evt, String[] tokens) {
        int minute = Integer.parseInt(tokens[0]);
        int seconds = Integer.parseInt(tokens[1]);

        int desiredMinute = minute * 60;
        int desiredSeconds = seconds % 60;

        if ((tokens[1]).length() == 1) {
            desiredSeconds *= 6;
        }

        int desiredTime = desiredMinute + desiredSeconds;
        AudioTrackUtils.seek(evt, desiredTime);
    }
}
