package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PauseCommand extends Command implements AudioTrackCommand {

    public PauseCommand(Database database) {
        super("pause", database);
        description = "Pauses the track player";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (AudioTrackUtils.canInteract(evt)) {
            var prefix = database.getPrefixForGuild(evt.getGuild().getId());
            var resumeCommandName = new ResumeCommand(database).getName();

            var injector = Guice.createInjector(new CoreBotModule());
            if (!injector.getInstance(AudioTrackManager.class)
                    .getGuildAudioPlayer(evt.getGuild())
                    .getPlayer()
                    .isPaused()) {
                AudioTrackUtils.pause(evt);
                evt.getChannel()
                        .sendMessage("Now paused. Type " + prefix + resumeCommandName + " to resume.")
                        .queue();
            } else {
                evt.getChannel()
                        .sendMessage("The bot is already paused. Type " + prefix + resumeCommandName + " to resume.")
                        .queue();
            }
        }
    }
}
