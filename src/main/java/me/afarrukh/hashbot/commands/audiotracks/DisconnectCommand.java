package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Inject;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DisconnectCommand extends Command implements AudioTrackCommand {

    private final JDA jda;

    public DisconnectCommand(JDA jda) {
        super("disconnect");
        this.jda = jda;
        addAlias("dc");
        addAlias("d");
        addAlias("leave");
        description = "Disconnects the bot if it is already in a voice channel";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!AudioTrackUtils.canInteract(evt)) return;

        if (evt.getGuild()
                .getMemberById(jda.getSelfUser().getId())
                .getVoiceState()
                .getChannel()
                .equals(evt.getMember().getVoiceState().getChannel())) {
            AudioTrackUtils.disconnect(evt.getGuild());
        }
    }
}
