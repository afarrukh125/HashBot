package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RemoveCommand extends Command implements MusicCommand {

    public RemoveCommand() {
        super("remove");
        addAlias("rm");
        addAlias("r");
        description = "Removes a song at the specified position on the queue";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        try {
            if (MusicUtils.canInteract(evt))
                MusicUtils.remove(evt, Integer.parseInt(params));
        } catch (NullPointerException | NumberFormatException e) {
            onIncorrectParams(evt.getTextChannel());
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: remove/rm/r <song index>").queue();
    }
}
