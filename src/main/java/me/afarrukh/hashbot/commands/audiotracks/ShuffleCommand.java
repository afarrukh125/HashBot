package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ShuffleCommand extends Command implements AudioTrackCommand {

    public ShuffleCommand(Database database) {
        super("shuffle", database);
        description = "Shuffles the track queue";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (AudioTrackUtils.canInteract(evt)) {
            var injector = Guice.createInjector(new CoreBotModule());
            injector.getInstance(AudioTrackManager.class)
                    .getGuildAudioPlayer(evt.getGuild())
                    .getScheduler()
                    .shuffleAndReplace();
            evt.getChannel().sendMessage("Shuffled the playlist :ok_hand:").queue();
        }
    }
}
