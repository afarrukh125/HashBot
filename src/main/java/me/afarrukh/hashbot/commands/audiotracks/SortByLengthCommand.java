package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class SortByLengthCommand extends Command {

    public SortByLengthCommand() {
        super("sortlength");
        description = "Sorts the remaining tracks in the track queue from shortest to longest";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        var injector = Guice.createInjector(new CoreBotModule());
        var trackManager = injector.getInstance(AudioTrackManager.class);
        List<AudioTrack> tracks =
                trackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().getAsArrayList();
        var botUser = injector.getInstance(JDA.class);
        if (!requireNonNull(requireNonNull(evt.getGuild()
                                .getMemberById(botUser.getSelfUser().getId()))
                        .getVoiceState())
                .inAudioChannel()) {
            evt.getChannel().sendMessage("Bot is not in channel").queue();
            return;
        }

        if (!requireNonNull(requireNonNull(evt.getMember()).getVoiceState()).inAudioChannel()) {
            evt.getChannel().sendMessage("You are not in a voice channel").queue();
            return;
        }

        if (!evt.getMember()
                .getVoiceState()
                .getChannel()
                .equals(evt.getGuild()
                        .getMemberById(botUser.getSelfUser().getId())
                        .getVoiceState()
                        .getChannel())) {
            evt.getChannel()
                    .sendMessage("You are not in the same channel as the bot")
                    .queue();
            return;
        }

        if (!tracks.isEmpty()) {
            tracks.sort((o1, o2) -> -Long.compare(o2.getDuration(), o1.getDuration()));
            trackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().replaceQueue(tracks);
            evt.getChannel()
                    .sendMessage("The queue has been sorted in order of size")
                    .queue();
        } else {
            evt.getChannel()
                    .sendMessage("There are currently no songs left to be queued")
                    .queue();
        }
    }
}
