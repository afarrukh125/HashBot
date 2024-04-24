package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PauseCommand extends Command implements AudioTrackCommand {

    private final Database database;

    public PauseCommand(Database database) {
        super("pause");
        this.database = database;
        description = "Pauses the track player";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (AudioTrackUtils.canInteract(evt)) {
            var prefix = database.getPrefixForGuild(evt.getGuild().getId());
            var resumeCommandName = new ResumeCommand().getName();
            if (!Bot.trackManager
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
