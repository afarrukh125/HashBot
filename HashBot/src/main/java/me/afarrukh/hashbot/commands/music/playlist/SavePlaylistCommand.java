package me.afarrukh.hashbot.commands.music.playlist;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.SQLUserDataManager;
import me.afarrukh.hashbot.exceptions.PlaylistException;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.*;

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
        if (!MusicUtils.canInteract(evt))
            return;
        if (params == null) {
            evt.getTextChannel().sendMessage("You must provide a name for the playlist").queue();
            return;
        }

        List<AudioTrack> trackList = new ArrayList<>();
        trackList.add(Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().getPlayingTrack());
        trackList.addAll(Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().getArrayList());

        // Ensuring all tracks in the list are unique
        Map<String, String> uriNameMap = new HashMap<>();

        for(AudioTrack track: trackList) {
            uriNameMap.put(track.getInfo().uri, track.getInfo().title);
        }

        if (uriNameMap.keySet().size() < 2) {
            evt.getTextChannel().sendMessage("You must have at least 1 track playing, and 1 track in the queue (so 2 total) to create a playlist").queue();
            return;
        }
        if (uriNameMap.keySet().size() > 100) {
            evt.getTextChannel().sendMessage("You can only save playlists that have 100 tracks or less.").queue();
            return;
        }

        Message message = evt.getTextChannel().sendMessage("Creating playlist " + params + " with " + uriNameMap.keySet().size() + " tracks.").complete();

        try {
            new SQLUserDataManager(evt.getMember()).addPlaylist(params, uriNameMap);

            message.editMessage("You have successfully created the playlist " + params + " with " + uriNameMap.keySet().size() + " tracks.").queue();
        } catch (PlaylistException e) {
            message.editMessage("The name you have selected for this playlist is already in use. " +
                    "Please choose another").queue();
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
