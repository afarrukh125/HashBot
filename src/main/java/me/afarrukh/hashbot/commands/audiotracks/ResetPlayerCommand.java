package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ResetPlayerCommand extends Command implements AudioTrackCommand, AdminCommand {

    private final AudioTrackManager audioTrackManager;

    public ResetPlayerCommand(AudioTrackManager audioTrackManager) {
        super("resetplayer");
        this.audioTrackManager = audioTrackManager;
        addAlias("rp");
        description =
                "Resets the track player for this guild. Use if any issues are occurring with the track player. Admin only command.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!evt.getMember().hasPermission(Permission.ADMINISTRATOR)) return;

        AudioTrackUtils.disconnect(evt.getGuild(), audioTrackManager);
        audioTrackManager.resetGuildAudioPlayer(evt.getGuild());
        evt.getChannel()
                .sendMessage("Reset the audio player for this guild successfully.")
                .queue();
    }
}
