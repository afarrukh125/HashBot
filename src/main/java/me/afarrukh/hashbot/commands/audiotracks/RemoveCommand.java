package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemoveCommand extends Command implements AudioTrackCommand {

    private final Database database;
    private final AudioTrackManager audioTrackManager;

    public RemoveCommand(Database database, AudioTrackManager audioTrackManager) {
        super("remove");
        this.database = database;
        this.audioTrackManager = audioTrackManager;
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
                AudioTrackUtils.remove(evt, Integer.parseInt(params), audioTrackManager);
        } catch (NullPointerException | NumberFormatException e) {
            onIncorrectParams(database, evt.getChannel().asTextChannel());
        }
    }

    @Override
    public void onIncorrectParams(Database database, TextChannel channel) {
        channel.sendMessage("Usage: remove/rm/r <track index>").queue();
    }
}
