package me.afarrukh.hashbot.music;

import me.afarrukh.hashbot.commands.music.playlist.LoadListCommand;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Abdullah
 * Created on 14/09/2019 at 14:24
 * <p>
 * This aims to be the barrier for the tracks entering. We have used LatentTrack object, as a wrapper around the
 * AudioTrack object that is to be queued, so we can keep track of the index of the playlist we are in.
 * <p>
 * The whole purpose of this class is to prevent the tracks from being loaded "out of order"
 * <p>
 * For instance, assuming that this class was not there to load all the tracks at once, consider the scenario with our
 * two users, A and B. User A queues a playlist and this is being loaded asynchronously. Now user B loads
 * another playlist. What will happen now, is that the tracks will be interleaved in an arbitrary order.
 * <p>
 * This kind of behaviour is undesirable. Typically when a user queues a playlist, it should be made sure that the
 * tracks in the playlist are queued in the original order.
 * <p>
 * There is a caveat however, with this system, and a compromise was made. We queue the first track in the list preemptively.
 * This means that, although every track in the playlist beyond the first is queued in the original order, there may be tracks
 * queued inbetween the first and the second track in the list. Check the other classes and methods that are used in coordinating
 * this behaviour
 * @see LatentTrack
 * @see me.afarrukh.hashbot.data.SQLUserDataManager#loadPlaylistByName(String, PlaylistLoader)
 * @see LoadListCommand
 */
public class PlaylistLoader {

    /**
     * The list of <code>AudioTrack</code> objects to be queued.
     */
    private final List<LatentTrack> tracks;
    /**
     * The associated member object, for use in deciding which guild to send messages to,
     * and which member to join
     */
    private final Member member;
    /**
     * The message object to update once the playlist has completed loading
     */
    private final Message message;
    /**
     * The name of the playlist. For use in printing the final message to be sent to the user once the playlist
     * has finished loading
     */
    private final String listName;
    /**
     * The original size of the playlist
     */
    private final int originalSize;
    /**
     * The original size of the playlist
     * We aim to count upwards towards this as we add tracks to this "barrier"
     */
    private int maxSize;
    /**
     * The current index of the playlist, as mentioned, this is counting up towards maxSize (well technically maxSize-1)
     */
    private int currentIndex;

    /**
     * Create a new playlist loader object
     *
     * @param member   The associated member object with this loader
     * @param maxSize  The maximum size of the playlist
     * @param message  The associated message object to be updated once the playlist has been loaded
     * @param listName The name of the playlist
     */
    public PlaylistLoader(Member member, int maxSize, Message message, String listName) {
        this.maxSize = maxSize;
        this.member = member;
        this.tracks = new ArrayList<>();
        this.currentIndex = 0;
        this.message = message;
        this.listName = listName;
        originalSize = maxSize;
    }

    /**
     * This deals with adding tracks to the track list. This keeps track of all the tracks that have been added
     * to the track list. Once the size of the track list matches the maximum intended size of the playlist,
     * the tracks are let out of the "barrier", into the live track queue on the bot
     *
     * @param track The track to be added to the queue. Note that this is not an AudioTrack, and is actually a
     *              <code>LatentTrack object</code>
     * @throws InterruptedException This is thrown naturally by the wait() function call. We don't handle it here
     */
    public synchronized void addTrack(LatentTrack track) throws InterruptedException {

        // We want to add the first track, so in case there are no tracks in the queue, the user can listen to the first
        // in the meantime
        while (currentIndex != track.getPos())
            wait();

        tracks.add(track);
        currentIndex++;
        notifyAll();


        if (currentIndex == maxSize - 1) { // We take away 1 because we queued one track pre-emptively
            queueTracks();
            MusicUtils.connectToChannel(member);
            message.editMessage("Completed loading " + maxSize + " tracks from " + listName).queue(message -> {
                if (maxSize != originalSize)
                    message.editMessage(message.getContentRaw() + ". \nFailed to load " + (originalSize - maxSize) + " tracks because of YouTube copyright errors.").queue();
            });

            notifyAll();
        } else {
            if(currentIndex % Constants.PLAYLIST_UPDATE_INTERVAL == 0)
            message.editMessage("Queueing playlist " + listName + " with " + originalSize + " tracks." +
                    " It might take a while for all tracks to be added to the queue... (" + currentIndex + "/" + originalSize + ")").queue();
        }
    }

    /**
     * This is the final procedure to be called by this class upon completion.
     * This essentially dumps all the latent tracks into the track queue, after which the purpose of this class is complete.
     */
    private void queueTracks() {
        Bot.musicManager.getGuildAudioPlayer(member.getGuild()).getScheduler().queue(tracks);
        System.gc();
    }

    public void notifyFailed() {
        maxSize--;
        currentIndex++;
    }
}
