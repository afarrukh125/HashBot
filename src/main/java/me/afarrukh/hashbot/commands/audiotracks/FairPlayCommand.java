package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.track.TrackScheduler;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class FairPlayCommand extends Command implements AudioTrackCommand {

    public FairPlayCommand() {
        super("fairplay");
        addAlias("fp");
        description =
                "If this is turned on, tracks are automatically queued and sorted so everyone gets an equal chance to queue.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!AudioTrackUtils.canInteract(evt)) {
            return;
        }
        var injector = Guice.createInjector(new CoreBotModule());

        TrackScheduler ts = injector.getInstance(Bot.class)
                .getTrackManager()
                .getGuildAudioPlayer(evt.getGuild())
                .getScheduler();

        if (ts.isLoopingQueue()) {
            evt.getChannel()
                    .sendMessage("Cannot use this feature unless looping queue is disabled.")
                    .queue();
            return;
        }

        ts.setFairPlay(!ts.isFairPlay());

        StringBuilder sb = new StringBuilder();
        sb.append("Fairplay mode is now ");
        final String onOrOff = ts.isFairPlay() ? "on" : "off";
        sb.append(onOrOff).append(".");
        evt.getChannel().sendMessage(sb.toString()).queue();
    }
}
