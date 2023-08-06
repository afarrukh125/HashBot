package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import com.google.inject.Inject;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DisconnectCommand extends Command implements AudioTrackCommand {


    private final AudioTrackManager audioTrackManager;

    @Inject
    public DisconnectCommand(Database database, AudioTrackManager audioTrackManager) {
        super("disconnect", database);
        this.audioTrackManager = audioTrackManager;
        addAlias("dc");
        addAlias("d");
        addAlias("leave");
        description = "Disconnects the bot if it is already in a voice channel";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!AudioTrackUtils.canInteract(evt)) {
            return;
        }

        if (evt.getGuild()
                .getMemberById(evt.getJDA().getSelfUser().getId())
                .getVoiceState()
                .getChannel()
                .equals(evt.getMember().getVoiceState().getChannel())) {
            AudioTrackUtils.disconnect(evt.getGuild(), audioTrackManager);
        }
    }
}
