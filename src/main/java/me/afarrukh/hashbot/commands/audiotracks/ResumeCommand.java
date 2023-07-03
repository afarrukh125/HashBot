package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ResumeCommand extends Command implements AudioTrackCommand {

    public ResumeCommand() {
        super("resume");
        addAlias("res");
        description = "Resumes the track player";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (AudioTrackUtils.canInteract(evt)) {
            var injector = Guice.createInjector(new CoreBotModule());
            var trackManager = injector.getInstance(AudioTrackManager.class);
            if (trackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().isPaused()) {
                AudioTrackUtils.resume(evt);
                evt.getChannel().sendMessage("Resumed.").queue();
            } else evt.getChannel().sendMessage("The bot is already playing.").queue();
        }
    }
}
