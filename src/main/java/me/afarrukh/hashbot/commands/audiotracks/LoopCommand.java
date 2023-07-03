package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LoopCommand extends Command implements AudioTrackCommand {

    public LoopCommand() {
        super("loop");
        description = "Loops the currently playing track. Will no longer loop if next track is skipped to";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (AudioTrackUtils.canInteract(evt)) {
            var injector = Guice.createInjector(new CoreBotModule());
            var trackManager = injector.getInstance(Bot.class).getTrackManager();
            var trackScheduler =
                    trackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler();
            trackScheduler.setLooping(!trackScheduler.isLooping());
            String status;
            if (trackScheduler.isLooping()) {
                status = "Now";
            } else {
                status = "No longer";
            }
            evt.getChannel()
                    .sendMessage(status + " looping: `"
                            + trackManager
                                    .getGuildAudioPlayer(evt.getGuild())
                                    .getPlayer()
                                    .getPlayingTrack()
                                    .getInfo()
                                    .title
                            + "`")
                    .queue();
        }
    }
}
