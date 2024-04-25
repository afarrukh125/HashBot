package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ResumeCommand extends Command implements AudioTrackCommand {

    private final AudioTrackManager audioTrackManager;

    public ResumeCommand(AudioTrackManager audioTrackManager) {
        super("resume");
        this.audioTrackManager = audioTrackManager;
        addAlias("res");
        description = "Resumes the track player";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (AudioTrackUtils.canInteract(evt)) {
            if (audioTrackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().isPaused()) {
                AudioTrackUtils.resume(evt, audioTrackManager);
                evt.getChannel().sendMessage("Resumed.").queue();
            } else evt.getChannel().sendMessage("The bot is already playing.").queue();
        }
    }
}
