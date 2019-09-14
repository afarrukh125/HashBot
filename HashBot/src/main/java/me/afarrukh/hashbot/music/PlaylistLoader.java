package me.afarrukh.hashbot.music;

import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.exceptions.PlaylistException;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Abdullah
 * Created on 14/09/2019 at 14:24
 *
 * This aims to be the barrier for the tracks entering. We have used LatentTrack object, as a wrapper around the
 * AudioTrack object that is to be queued, so we can keep track of the index of the playlist we are in.
 *
 * The whole purpose of this class is to prevent the tracks from being loaded "out of order"
 *
 * For instance, assuming that this class was not there to load all the tracks at once, consider the scenario with our
 * two users, A and B. User A queues a playlist and this is being loaded asynchronously. Now user B loads
 * another playlist. What will happen now, is that the tracks will be interleaved in an arbitrary order.
 *
 * This kind of behaviour is undesirable. Typically when a user queues a playlist, it should be made sure that the
 * tracks in the playlist are queued in the original order.
 *
 * There is a caveat however, with this system, and a compromise was made. We queue the first track in the list preemptively.
 * This means that, although every track in the playlist beyond the first is queued in the original order, there may be tracks
 * queued inbetween the first and the second track in the list. Check the other classes and methods that are used in coordinating
 * this behaviour
 *
 * @see LatentTrack
 * @see me.afarrukh.hashbot.data.SQLUserDataManager#loadPlaylistByName(String, PlaylistLoader)
 * @see me.afarrukh.hashbot.commands.music.LoadListCommand
 *
 */
public class PlaylistLoader {

    /**
     * The list of <code>AudioTrack</code> objects to be queued.
     */
    private List<LatentTrack> tracks;
    private int maxSize;
    private Member member;
    private Message message;

    private int currentIndex;

    private String listName;

    public PlaylistLoader(Member member, int maxSize, Message message, String listName) {
        this.maxSize = maxSize;
        this.member = member;
        this.tracks = new ArrayList<>();
        this.currentIndex = 0;
        this.message = message;
        this.listName = listName;
    }

    public synchronized void addTrack(LatentTrack track) throws InterruptedException {

        // We want to add the first track, so in case there are no tracks in the queue, the user can listen to the first
        // in the meantime
        while (currentIndex != track.getPos())
            wait();

        tracks.add(track);
        currentIndex++;
        notifyAll();


        if(currentIndex == maxSize-1) { // We take away 1 because it is likely we queued one song pre-emptively
            try {
                queueTracks();
                MusicUtils.connectToChannel(member);
                message.editMessage("Completed loading " + maxSize + " tracks from " + listName).queue();
            } catch (PlaylistException e) {
                e.printStackTrace();
            }
            notifyAll();
        }
    }

    public void queueTracks() throws PlaylistException {
        if(tracks.size() != maxSize-1) {
            throw new PlaylistException("You cannot obtain the tracks until the list has finished loading");
        }

        Bot.musicManager.getGuildAudioPlayer(member.getGuild()).getScheduler().queue(tracks);
    }
}
