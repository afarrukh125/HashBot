package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ResetPlayerCommand extends Command implements AudioTrackCommand, AdminCommand {

    public ResetPlayerCommand() {
        super("resetplayer");
        addAlias("rp");
        description =
                "Resets the track player for this guild. Use if any issues are occurring with the track player. Admin only command.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!evt.getMember().hasPermission(Permission.ADMINISTRATOR)) return;

        AudioTrackUtils.disconnect(evt.getGuild());
        Bot.trackManager.resetGuildAudioPlayer(evt.getGuild());
        evt.getChannel()
                .sendMessage("Reset the audio player for this guild successfully.")
                .queue();
    }
}
