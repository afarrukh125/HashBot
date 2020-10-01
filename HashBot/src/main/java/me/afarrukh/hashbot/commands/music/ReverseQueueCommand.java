package me.afarrukh.hashbot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class ReverseQueueCommand extends Command {

    public ReverseQueueCommand() {
        super("reversequeue");
        addAlias("rq");
        description = "Inverts the queue contents so that the last track is first and the first track is last. " +
                "Does not affect the currently playing track.";
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {

        if(MusicUtils.canInteract(evt)) {

            List<AudioTrack> tracks = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().getArrayList();
            List<AudioTrack> reversedTracks = new ArrayList<>();

            for (int i = tracks.size() - 1; i >= 0; i--)
                reversedTracks.add(tracks.get(i));


            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().replaceQueue(reversedTracks);

            evt.getChannel().sendMessage("The queue has been reversed").queue();
        }
    }
}
