package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.OwnerCommand;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SetVolumeCommand extends Command implements OwnerCommand {

    public SetVolumeCommand() {
        super("setvolume");
        addAlias("sv");
        description = "Sets the volume to the desired value.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(params == null) {
            evt.getTextChannel().sendMessage("Need to provide volume to change to.").queue();
            return;
        }
        try {
            MusicUtils.setVolume(evt, Integer.parseInt(params));
        } catch (NumberFormatException e) {
            evt.getTextChannel().sendMessage("Need to provide an integer as the volume to set to").queue();
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
