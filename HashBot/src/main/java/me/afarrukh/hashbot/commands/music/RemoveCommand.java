package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author Abdullah
 * <p>
 * Removes a track at the given index from the track queue from the {@link me.afarrukh.hashbot.music.TrackScheduler}
 * @see me.afarrukh.hashbot.music.TrackScheduler
 */
public class RemoveCommand extends Command implements MusicCommand {

    public RemoveCommand() {
        super("remove");
        addAlias("rm");
        addAlias("r");
        description = "Removes a track at the specified position on the queue";
        addParameter("position", "The position at which the track to be removed is");
        addExampleUsage("remove 10");
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        try {
            if (MusicUtils.canInteract(evt))
                MusicUtils.remove(evt, Integer.parseInt(params));
        } catch (NullPointerException | NumberFormatException e) {
            onIncorrectParams(evt.getChannel());
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: remove/rm/r <track index>").queue();
    }
}
