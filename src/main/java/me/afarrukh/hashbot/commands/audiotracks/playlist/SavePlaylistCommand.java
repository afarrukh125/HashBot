package me.afarrukh.hashbot.commands.audiotracks.playlist;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

public class SavePlaylistCommand extends Command implements AudioTrackCommand {

    public SavePlaylistCommand() {
        super("savelist");
        addAlias("saveplaylist");
        addAlias("save");
        addAlias("spl");

        description = "Save the current playlist, given a name";

        addParameter("list name", "The name of the new playlist to be created");
        addParameter(
                "FLAG: idx",
                "The index of the current queue to save the playlist from, loops round to the start and adds all songs regardless");
        addExampleUsage("savelist list");
        addExampleUsage(
                "savelist list -idx 7 (Note: This starts from the index 7 and runs til the end of the list, then starts from the playing song up to 6)");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!AudioTrackUtils.canInteract(evt)) {
            return;
        }

        int startIndex = 0;

        // Handle the flag
        if (params.contains("-idx")) {
            String[] tokens = params.split(" ");
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (; i < tokens.length; i++) {
                String nameString = tokens[i];
                if (nameString.equals("-idx")) break;
                sb.append(nameString).append(" ");
            }

            params = sb.toString().trim();
            try {
                startIndex = Integer.parseInt(tokens[i + 1]);
            } catch (Exception e) {
                evt.getChannel()
                        .sendMessage(
                                "The index to start the list from is not specified correctly, or exceeds the length of the track queue.")
                        .queue();
                return;
            }
        }

        if (params.equals("")) {
            evt.getChannel()
                    .sendMessage("You must provide a name for the playlist")
                    .queue();
            return;
        }

        List<AudioTrack> trackList = new ArrayList<>();
        trackList.add(
                Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().getPlayingTrack());
        trackList.addAll(Bot.trackManager
                .getGuildAudioPlayer(evt.getGuild())
                .getScheduler()
                .getAsArrayList());

        if (startIndex
                > Bot.trackManager
                .getGuildAudioPlayer(evt.getGuild())
                .getScheduler()
                .getQueue()
                .size()) {
            evt.getChannel()
                    .sendMessage("The index provided is higher than the number of tracks in the track queue.")
                    .queue();
            return;
        }

        // Ensuring all tracks in the list are unique
        Map<String, TrackData> trackDataMap = new LinkedHashMap<>();
        int added = 0;

        Map<String, String> userIdMap = new HashMap<>();

        for (int i = startIndex; added != trackList.size(); i++, added++) {

            if (i >= trackList.size()) {
                i = 0;
            }

            AudioTrack track = trackList.get(i);
            String userName = track.getUserData(String.class);
            String id = userIdMap.get(userName);

            if (id == null) {
                id = evt.getGuild().getMembersByName(userName, true).get(0).getId();
                userIdMap.put(userName, id);
            }

            String trackUri = track.getInfo().uri;
            trackDataMap.put(trackUri, new TrackData(track.getInfo().title, id));
        }

        if (trackDataMap.keySet().size() < 2) {
            evt.getChannel()
                    .sendMessage(
                            "You must have at least 1 track playing, and 1 track in the queue (2 total) to create a playlist")
                    .queue();
            return;
        }

        if (trackDataMap.keySet().size() > Constants.CUSTOM_PLAYLIST_SIZE_LIMIT) {
            evt.getChannel()
                    .sendMessage("You can only save playlists that have " + Constants.CUSTOM_PLAYLIST_SIZE_LIMIT
                            + " tracks or less.")
                    .queue();
            return;
        }

        var database = Database.getInstance();
        var userId = evt.getMember().getId();

        var playlistName = params;
        var maybePlaylist = database.getPlaylistForUser(playlistName, userId);

        if (maybePlaylist.isPresent()) {
            evt.getChannel()
                    .sendMessage("The name you have selected for this playlist is already in use. "
                            + "Please choose another")
                    .queue();
        } else {
            database.createPlaylistForUser(userId, playlistName, trackDataMap);
            evt.getChannel()
                    .sendMessage("You have successfully created the playlist `" + params + "` with "
                            + trackDataMap.keySet().size() + " tracks.")
                    .queue();
        }
    }
}
