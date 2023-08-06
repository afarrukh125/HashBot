package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import com.google.inject.Inject;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ClearQueueCommand extends Command implements AudioTrackCommand {

    @Inject
    public ClearQueueCommand(Database database) {
        super("clearqueue", database);
        addAlias("cq");
        description = "Clears the current queue for the track player";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!AudioTrackUtils.canInteract(evt)) {
            return;
        }

        var injector = Guice.createInjector(new CoreBotModule());
        AudioTrackManager trackManager = injector.getInstance(Bot.class).getTrackManager();
        if (trackManager
                .getGuildAudioPlayer(evt.getGuild())
                .getScheduler()
                .getQueue()
                .isEmpty()) {
            evt.getChannel().sendMessage("Queue is empty - nothing cleared").queue();
            return;
        }
        trackManager
                .getGuildAudioPlayer(evt.getGuild())
                .getScheduler()
                .getQueue()
                .clear();
        evt.getChannel().sendMessage("Queue cleared :ok_hand:").queue();
    }
}
