package me.afarrukh.hashbot.track;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;

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
    private final AudioTrackManager audioTrackManager;
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
    public PlaylistLoader(AudioTrackManager audioTrackManager, Member member, int maxSize, Message message, String listName) {
        this.audioTrackManager = audioTrackManager;
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
        while (currentIndex != track.getPos()) {
            wait();
        }

        tracks.add(track);
        currentIndex++;
        notifyAll();

        if (currentIndex == maxSize - 1) { // We take away 1 because we queued one track pre-emptively
            queueTracks();
            AudioTrackUtils.connectToChannel(member);
            message.editMessage("Completed loading " + maxSize + " tracks from `" + listName + "`")
                    .queue(message -> {
                        if (maxSize != originalSize)
                            message.editMessage(message.getContentRaw() + ". \nFailed to load "
                                            + (originalSize - maxSize) + " tracks because of YouTube copyright errors.")
                                    .queue();
                    });

            notifyAll();
        } else {
            if (currentIndex % Constants.PLAYLIST_UPDATE_INTERVAL == 0)
                message.editMessage("Queueing playlist " + listName + " with " + originalSize + " tracks."
                                + " It might take a while for all tracks to be added to the queue... (" + currentIndex
                                + "/" + originalSize + ")")
                        .queue();
        }
    }

    /**
     * This is the final procedure to be called by this class upon completion.
     * This essentially dumps all the latent tracks into the track queue, after which the purpose of this class is complete.
     */
    private void queueTracks() {
        audioTrackManager.getGuildAudioPlayer(member.getGuild()).getScheduler().queue(tracks);
        System.gc();
    }

    public void notifyFailed() {
        maxSize--;
        currentIndex++;
    }
}
