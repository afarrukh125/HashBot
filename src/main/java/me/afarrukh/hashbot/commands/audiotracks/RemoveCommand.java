package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author Abdullah
 * <p>
 * Removes a track at the given index from the track queue from the {@link me.afarrukh.hashbot.track.TrackScheduler}
 * @see me.afarrukh.hashbot.track.TrackScheduler
 */
public class RemoveCommand extends Command implements AudioTrackCommand {

    public RemoveCommand() {
        super("remove");
        addAlias("rm");
        addAlias("r");
        description = "Removes a track at the specified position on the queue";
        addParameter("position", "The position at which the track to be removed is");
        addExampleUsage("remove 10");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        try {
            if (AudioTrackUtils.canInteract(evt))
                AudioTrackUtils.remove(evt, Integer.parseInt(params));
        } catch (NullPointerException | NumberFormatException e) {
            onIncorrectParams(evt.getTextChannel());
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: remove/rm/r <track index>").queue();
    }
}