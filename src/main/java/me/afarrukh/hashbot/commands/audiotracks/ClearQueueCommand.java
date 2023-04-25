package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ClearQueueCommand extends Command implements AudioTrackCommand {

    public ClearQueueCommand() {
        super("clearqueue");
        addAlias("cq");
        description = "Clears the current queue for the track player";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!AudioTrackUtils.canInteract(evt)) return;

        if (Bot.trackManager
                .getGuildAudioPlayer(evt.getGuild())
                .getScheduler()
                .getQueue()
                .isEmpty()) {
            evt.getChannel()
                    .sendMessage("Nothing is in the queue to be cleared.")
                    .queue();
            return;
        }
        Bot.trackManager
                .getGuildAudioPlayer(evt.getGuild())
                .getScheduler()
                .getQueue()
                .clear();
        evt.getChannel().sendMessage("Queue cleared :ok_hand:").queue();
    }
}
