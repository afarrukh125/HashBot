package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.music.TrackScheduler;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class LoopQueueCommand extends Command implements MusicCommand {

    public LoopQueueCommand() {
        super("loopqueue");
        addAlias("lq");
        description = "Adds tracks to the end of the queue once they are complete.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (MusicUtils.canInteract(evt)) {
            TrackScheduler ts = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler();
            if (ts.isFairPlay()) {
                evt.getTextChannel().sendMessage("Cannot use this feature unless fairplay is disabled.").queue();
                return;
            }
            ts.setLoopingQueue(!ts.isLoopingQueue());
            String string = (ts.isLoopingQueue() ? "Now looping the queue. Tracks will be added" +
                    " to the back of the queue." : "No longer looping the queue.");
            evt.getTextChannel().sendMessage(string).queue();
        }
    }
}
