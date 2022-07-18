package me.afarrukh.hashbot.commands.audiotracks;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class SortByLengthCommand extends Command {

    public SortByLengthCommand() {
        super("sortlength");
        description = "Sorts the remaining tracks in the track queue from shortest to longest";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        List<AudioTrack> tracks = Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().getAsArrayList();

        if(!Objects.requireNonNull(Objects.requireNonNull(evt.getGuild().getMemberById(Bot.botUser.getSelfUser().getId())).getVoiceState()).inAudioChannel()) {
            evt.getChannel().sendMessage("Bot is not in channel").queue();
            return;
        }

        if(!Objects.requireNonNull(Objects.requireNonNull(evt.getMember()).getVoiceState()).inAudioChannel()) {
            evt.getChannel().sendMessage("You are not in a voice channel").queue();
            return;
        }

        if(!evt.getMember().getVoiceState().getChannel().equals(evt.getGuild().getMemberById(Bot.botUser.getSelfUser().getId()).getVoiceState().getChannel())) {
            evt.getChannel().sendMessage("You are not in the same channel as the bot").queue();
            return;
        }

        if(!tracks.isEmpty()) {
            tracks.sort((o1, o2) -> -Long.compare(o2.getDuration(), o1.getDuration()));
            Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().replaceQueue(tracks);
            evt.getChannel().sendMessage("The queue has been sorted in order of size").queue();
        } else {
            evt.getChannel().sendMessage("There are currently no songs left to be queued").queue();
        }
    }
}
