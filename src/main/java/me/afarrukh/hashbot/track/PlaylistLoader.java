package me.afarrukh.hashbot.track;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;

public class PlaylistLoader {

    private final List<LatentTrack> tracks;

    private final Member member;

    private final Message message;

    private final String listName;

    private final int originalSize;

    private int maxSize;

    private int currentIndex;

    public PlaylistLoader(Member member, int maxSize, Message message, String listName) {
        this.maxSize = maxSize;
        this.member = member;
        this.tracks = new ArrayList<>();
        this.currentIndex = 0;
        this.message = message;
        this.listName = listName;
        originalSize = maxSize;
    }

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
        Bot.trackManager.getGuildAudioPlayer(member.getGuild()).getScheduler().queue(tracks);
        System.gc();
    }

    public void notifyFailed() {
        maxSize--;
        currentIndex++;
    }
}
