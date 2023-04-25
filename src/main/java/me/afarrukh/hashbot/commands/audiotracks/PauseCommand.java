package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PauseCommand extends Command implements AudioTrackCommand {

    public PauseCommand() {
        super("pause");
        description = "Pauses the track player";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (AudioTrackUtils.canInteract(evt)) {
            ResumeCommand resumeCommand = new ResumeCommand();
            if (!Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().isPaused()) {
                AudioTrackUtils.pause(evt);
                evt.getChannel().sendMessage("Now paused. Type " + Bot.prefixManager.getGuildRoleManager(evt.getGuild()).getPrefix()
                        + resumeCommand.getName() + " to resume.").queue();
            } else
                evt.getChannel().sendMessage("The bot is already paused. Type " + Bot.prefixManager.getGuildRoleManager(evt.getGuild()).getPrefix()
                        + resumeCommand.getName() + " to resume.").queue();
        }
    }
}
