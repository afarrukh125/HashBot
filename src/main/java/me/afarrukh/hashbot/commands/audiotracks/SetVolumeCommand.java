package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.OwnerCommand;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetVolumeCommand extends Command implements OwnerCommand {

    public SetVolumeCommand() {
        super("setvolume");
        addAlias("sv");
        description = "Sets the volume to the desired value.";
        addParameter("volume", "The volume to set to");
        addExampleUsage("setvolume 77");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (params == null) {
            evt.getChannel().sendMessage("Need to provide volume to change to.").queue();
            return;
        }
        try {
            AudioTrackUtils.setVolume(evt, Integer.parseInt(params));
        } catch (NumberFormatException e) {
            evt.getChannel().sendMessage("Need to provide an integer as the volume to set to").queue();
        }
    }
}
