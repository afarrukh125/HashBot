package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class DisconnectCommand extends Command {
    public DisconnectCommand() {
        super("disconnect", new String[]{"dc", "d"});
        description = "Disconnects the bot if it is already in a voice channel";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(MusicUtils.canInteract(evt))
            MusicUtils.disconnect(evt.getGuild());
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
