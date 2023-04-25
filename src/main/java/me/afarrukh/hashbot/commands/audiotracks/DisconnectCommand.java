package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DisconnectCommand extends Command implements AudioTrackCommand {
    public DisconnectCommand() {
        super("disconnect");
        addAlias("dc");
        addAlias("d");
        addAlias("leave");
        description = "Disconnects the bot if it is already in a voice channel";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!AudioTrackUtils.canInteract(evt)) return;

        if (evt.getGuild()
                .getMemberById(Bot.botUser().getSelfUser().getId())
                .getVoiceState()
                .getChannel()
                .equals(evt.getMember().getVoiceState().getChannel())) AudioTrackUtils.disconnect(evt.getGuild());
    }
}
