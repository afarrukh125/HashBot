package me.afarrukh.hashbot.utils;

import com.google.inject.Guice;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import me.afarrukh.hashbot.track.TrackScheduler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

@SuppressWarnings("Duplicates")
public class EmbedUtils {

    /**
     * @param gmm  The guild track manager
     * @param evt  The message received event associated with the queue message request
     * @param page The page of the message queue to be displayed
     * @return An embed referring to the current queue of audio tracks playing. If not found it simply goes to the method for a single track embed.
     */
    public static MessageEmbed getQueueMsg(GuildAudioTrackManager gmm, MessageReceivedEvent evt, int page) {
        try {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Constants.EMB_COL);
            eb.setTitle("Queue for " + evt.getGuild().getName() + "\n");
            AudioTrack currentTrack = gmm.getPlayer().getPlayingTrack();

            BlockingQueue<AudioTrack> queue = gmm.getScheduler().getQueue();
            int maxPageNumber = queue.size() / 10 + 1; // We need to know how many tracks are displayed per page

            // If there are no tracks in the queue then it will just give an embedded message for a single track.
            if (queue.size() == 0) {
                return getSingleTrackEmbed(gmm.getPlayer().getPlayingTrack(), evt);
            }

            // This block of code is to prevent the list from displaying a blank page as the last one
            if (queue.size() % 10 == 0) maxPageNumber--;

            if (page > maxPageNumber) {
                return new EmbedBuilder()
                        .setDescription("Page " + page + " out of bounds.")
                        .setColor(Constants.EMB_COL)
                        .build();
            }

            Iterator<AudioTrack> iter = gmm.getScheduler().getQueue().iterator();
            int startIdx = 1 + ((page - 1) * 10); // The start track on that page eg page 2 would give 11
            int targetIdx = page * 10; // The last track on that page, eg page 2 would give 20
            int count = 1;
            eb.appendDescription(
                    "__Now Playing:__\n[" + currentTrack.getInfo().title + "](" + currentTrack.getInfo().uri
                            + ") | (`" + CmdUtils.longToMMSS(currentTrack.getPosition()) + "/"
                            + CmdUtils.longToMMSS(currentTrack.getDuration()) + "`) `queued by: "
                            + currentTrack.getUserData().toString() + "`\n\n\n__Upcoming__\n\n");
            while (iter.hasNext()) {
                AudioTrack at = iter.next();
                if (count >= startIdx && count <= targetIdx) {
                    eb.appendDescription("`" + count + ".` [" + at.getInfo().title + "](" + at.getInfo().uri
                            + ") | (`" + CmdUtils.longToMMSS(at.getDuration()) + "`) `queued by: "
                            + at.getUserData().toString() + "`\n\n");
                }
                if (count == targetIdx) break;

                count++;
            }
            eb.appendDescription("\n**" + queue.size() + " tracks queued, 1 playing** | Total duration**: `"
                    + gmm.getScheduler().getTotalQueueTime() + "` | **");
            StringBuilder sb = new StringBuilder();
            sb.append("[Page ").append(page).append("/").append(maxPageNumber).append("] ");
            if (gmm.getScheduler().isFairPlay())
                sb.append("Fairplay mode is on. Playtop command will not work as expected. Tracks are queued fairly. ");
            if (gmm.getScheduler().isLoopingQueue())
                sb.append("The queue is looping. Tracks will be added to the back of the queue once they finish");
            eb.setFooter(sb.toString(), evt.getAuthor().getAvatarUrl());
            eb.setThumbnail(AudioTrackUtils.getThumbnailURL(currentTrack));
            return eb.build();
        } catch (NullPointerException e) {
            return getNothingPlayingEmbed();
        }
    }

    /**
     * Returns an embed with the only track currently in the queue
     *
     * @param evt The event to get the channel to send it to
     * @return A message embed with information on a single provided audio track
     */
    public static MessageEmbed getSingleTrackEmbed(AudioTrack currentTrack, MessageReceivedEvent evt) {
        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder sb = new StringBuilder(); // Building the title
        sb.append("Currently playing");
        var injector = Guice.createInjector(new CoreBotModule());
        var trackManager = injector.getInstance(AudioTrackManager.class);
        if (trackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().isLooping()) sb.append(" [Looping]");
        eb.setTitle(sb.toString());
        eb.appendDescription("[" + currentTrack.getInfo().title + "](" + currentTrack.getInfo().uri + ")\n\n");
        eb.appendDescription("**Channel**: `" + currentTrack.getInfo().author + "`\n");
        eb.appendDescription("**Queued by**: `" + currentTrack.getUserData().toString() + "`\n");
        eb.appendDescription("**Duration**: `" + CmdUtils.longToMMSS(currentTrack.getPosition()) + "/"
                + CmdUtils.longToMMSS(currentTrack.getDuration()) + "`\n\n");
        String trackBar = AudioTrackUtils.getAudioTrackBar(currentTrack);
        eb.appendDescription(trackBar);
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(AudioTrackUtils.getThumbnailURL(currentTrack));
        return eb.build();
    }

    /**
     * @param gmm The guild track manager associated with the embed being requested
     * @param at  The audio track which has been queued
     * @param evt The message received event containing information such as which channel to send to
     * @return an embed referring to a track which has been queued to an audioplayer already playing a track
     */
    public static MessageEmbed getQueuedEmbed(GuildAudioTrackManager gmm, AudioTrack at, MessageReceivedEvent evt) {
        TrackScheduler ts = gmm.getScheduler();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Now playing");
        eb.setColor(Constants.EMB_COL);
        eb.appendDescription("[" + at.getInfo().title + "](" + at.getInfo().uri + ")\n\n");
        eb.appendDescription("**Channel**: `" + at.getInfo().author + "`\n");
        eb.appendDescription("**Queued by**: `" + at.getUserData().toString() + "`\n");
        eb.appendDescription("**Duration**: `" + CmdUtils.longToMMSS(at.getDuration()) + "`\n");
        if (!ts.getQueue().isEmpty() || gmm.getPlayer().getPlayingTrack() != null) {
            eb.setTitle("Queued track");
            eb.appendDescription("**Position in queue**: `" + ts.getTrackIndex(at) + "`\n");
            eb.appendDescription("**Playing in approximately**: `" + ts.getTotalTimeTil(at) + "`\n");
            if (ts.isFairPlay())
                eb.setFooter(
                        "Fairplay mode is currently on. Use "
                                + Database.getInstance()
                                        .getPrefixForGuild(evt.getGuild().getId()) + "fairplay to turn it off.",
                        null);
        }
        eb.setThumbnail(AudioTrackUtils.getThumbnailURL(at));

        return eb.build();
    }

    /**
     * @param at The audio track which has been skipped to
     * @return Returns an embed referring to the track which has been skipped to
     */
    public static MessageEmbed getSkippedToEmbed(AudioTrack at) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Skipped to");
        eb.setColor(Constants.EMB_COL);
        eb.appendDescription("[" + at.getInfo().title + "](" + at.getInfo().uri + ")\n\n");
        eb.appendDescription("**Channel**: `" + at.getInfo().author + "`\n");
        eb.appendDescription("**Queued by**: `" + at.getUserData().toString() + "`\n");
        eb.appendDescription("**Duration**: `" + CmdUtils.longToMMSS(at.getDuration()) + "`\n");
        eb.setThumbnail(AudioTrackUtils.getThumbnailURL(at));
        return eb.build();
    }

    /**
     * @param at The audio track which has been skipped
     * @return an embed referring to the track which has been skipped, not to be confused with getSkippedToEmbed
     */
    public static MessageEmbed getSkippedEmbed(AudioTrack at) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Skipped track");
        eb.appendDescription("[" + at.getInfo().title + "](" + at.getInfo().uri + ")\n\n");
        eb.setColor(Constants.EMB_COL);

        eb.setThumbnail(AudioTrackUtils.getThumbnailURL(at));

        return eb.build();
    }

    /**
     * @param gmm The track manager to query
     * @param at  The audiotrack which is being added
     * @return A message embed with the appropriate information for a track that has been queued to the top
     */
    public static MessageEmbed getQueuedTopEmbed(GuildAudioTrackManager gmm, AudioTrack at) {
        TrackScheduler ts = gmm.getScheduler();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Queued track to top");
        eb.setColor(Constants.EMB_COL);
        eb.appendDescription("[" + at.getInfo().title + "](" + at.getInfo().uri + ")\n\n");
        eb.appendDescription("**Channel**: `" + at.getInfo().author + "`\n");
        eb.appendDescription("**Queued by**: `" + at.getUserData().toString() + "`\n");
        eb.appendDescription("**Duration**: `" + CmdUtils.longToMMSS(at.getDuration()) + "`\n");
        if (!ts.getQueue().isEmpty() || gmm.getPlayer().getPlayingTrack() != null) {
            eb.appendDescription("**Position in queue**: `1`\n");
            String totalTime =
                    CmdUtils.longToHHMMSS(gmm.getPlayer().getPlayingTrack().getDuration()
                            - gmm.getPlayer().getPlayingTrack().getPosition());
            eb.appendDescription("**Playing in approximately**: `" + totalTime + "`\n");
        }
        eb.setThumbnail(AudioTrackUtils.getThumbnailURL(at));

        return eb.build();
    }

    /**
     * Gets an embed that returns a playlist that has been queued
     *
     * @param playlist The playlist to be added
     * @return the MessageEmbed object to represent this playlist that has been queued
     */
    public static MessageEmbed getPlaylistEmbed(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if (firstTrack == null) firstTrack = playlist.getTracks().get(0);

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Playlist added: " + playlist.getName());
        eb.appendDescription("**Queued by**: `" + firstTrack.getUserData().toString() + "`\n");
        eb.appendDescription("**Number of tracks**: `" + playlist.getTracks().size() + "`\n");
        eb.appendDescription("**Total duration**: `" + AudioTrackUtils.getPlaylistDuration(playlist) + "`\n");

        eb.setThumbnail(AudioTrackUtils.getThumbnailURL(firstTrack));
        return eb.build();
    }

    /**
     * @return An embed which informs that nothing is playing right now
     */
    public static MessageEmbed getNothingPlayingEmbed() {
        return new EmbedBuilder()
                .setDescription("Nothing playing right now.")
                .setColor(Constants.EMB_COL)
                .build();
    }
}
