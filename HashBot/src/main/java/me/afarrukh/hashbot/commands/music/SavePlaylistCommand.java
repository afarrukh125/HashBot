package me.afarrukh.hashbot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.SQLUserDataManager;
import me.afarrukh.hashbot.exceptions.PlaylistException;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Abdullah
 * Created on 10/09/2019 at 21:43
 */
public class SavePlaylistCommand extends Command implements MusicCommand {

    public SavePlaylistCommand() {
        super("savelist");
        addAlias("saveplaylist");
        addAlias("save");
        addAlias("spl");

        description = "Save the current playlist, given a name";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(params == null) {
            evt.getTextChannel().sendMessage("You must provide a name for the playlist").queue();
            return;
        }

        List<AudioTrack> trackList = new ArrayList<>();
        trackList.add(Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().getPlayingTrack());
        trackList.addAll(Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().getArrayList());

        Message message = evt.getTextChannel().sendMessage("Creating playlist " + params + " with " + trackList.size() + " tracks.").complete();

        try {
            new SQLUserDataManager(evt.getMember()).addPlaylist(params, trackList);

            message.editMessage("You have successfully created the playlist " + params + " with " + trackList.size() + " tracks.").queue();
        } catch (PlaylistException e) {
            evt.getTextChannel().sendMessage("The name you have selected for this playlist is already in use. " +
                    "Please choose another").queue();
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
