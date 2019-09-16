package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.music.TrackScheduler;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class FairPlayCommand extends Command implements MusicCommand {

    public FairPlayCommand() {
        super("fairplay");
        addAlias("fp");
        description = "If this is turned on, tracks are automatically queued and sorted so everyone gets an equal chance to queue.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!MusicUtils.canInteract(evt))
            return;

        TrackScheduler ts = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler();

        if (ts.isLoopingQueue()) {
            evt.getTextChannel().sendMessage("Cannot use this feature unless looping queue is disabled.").queue();
            return;
        }

        ts.setFairPlay(!ts.isFairPlay());

        StringBuilder sb = new StringBuilder();
        sb.append("Fairplay mode is now ");
        final String onOrOff = ts.isFairPlay() ? "on" : "off";
        sb.append(onOrOff).append(".");
        evt.getTextChannel().sendMessage(sb.toString()).queue();
    }
}
