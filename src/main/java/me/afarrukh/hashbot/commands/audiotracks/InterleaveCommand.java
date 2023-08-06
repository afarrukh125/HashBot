package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InterleaveCommand extends Command implements AudioTrackCommand {

    public InterleaveCommand(Database database) {
        super("interleave", database);
        addAlias("il");
        description = "Interleaves the tracks in the queue so that all users get fair playback.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        var injector = Guice.createInjector(new CoreBotModule());
        if (AudioTrackUtils.canInteract(evt)) {
            injector.getInstance(Bot.class)
                    .getTrackManager()
                    .getGuildAudioPlayer(evt.getGuild())
                    .getScheduler()
                    .interleave(false);
            evt.getChannel().sendMessage("Interleaved the playlist :ok_hand:").queue();
        }
    }
}
