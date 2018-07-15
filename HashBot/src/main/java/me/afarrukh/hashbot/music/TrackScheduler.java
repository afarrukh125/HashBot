package me.afarrukh.hashbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.CmdUtils;
import me.afarrukh.hashbot.utils.DisconnectTimer;
import net.dv8tion.jda.core.entities.Guild;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Class that contains a queue of music to play onto the bot
 */
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private boolean looping;
    private final Guild guild;

    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.guild = guild;
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        looping = false;
    }

    /**
     * Queues the song onto the track and informs if it was immediately played or queued
     * @param track The AudioTrack to play
     * @return Returns a boolean corresponding
     */
    public void queue(AudioTrack track) {
        if(!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void nextTrack() {
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {

        //Invalidates the timer if it is already set and clears it if a new track starts playing
        Bot.musicManager.getGuildAudioPlayer(guild).getDisconnectTimer().cancel();
        Bot.musicManager.getGuildAudioPlayer(guild).setDisconnectTimer(new Timer());
    }

    /**
     * Decides what happens when a song ends
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the current one is finished and if the load failed
        if(isLooping()) {
            player.stopTrack();
            AudioTrack cloneTrack = track.makeClone();
            cloneTrack.setUserData(track.getUserData());
            player.startTrack(cloneTrack, false);
            return;
        }

        if(endReason.mayStartNext)
            nextTrack();

        //Starting a timer to disconnect which is cancelled if the queue starts any new songs
        Timer disconnectTimer = Bot.musicManager.getGuildAudioPlayer(guild).getDisconnectTimer();
        disconnectTimer.schedule(new DisconnectTimer(guild), Constants.DISCONNECT_DELAY*1000);
    }

    /**
     * Replaces the current queue with a new one
     * @param list The list to replace the current queue with
     */
    private void replaceQueue(ArrayList<AudioTrack> list) {
        queue.clear();
        queue.addAll(list);
        list.clear();
    }

    /**
     * Converts a BlockingQueue<> into an Arraylist<>
     * @return The current track list in an arraylist format
     */
    public ArrayList<AudioTrack> getArrayList() {
        //Convert the queue to arraylist
        return new ArrayList<>(queue);
    }

    /**
     * Gets the duration of the queue's AudioTracks and the remaining time of the current song
     * @return
     */
    public String getTotalQueueTime() {
        long count = 0;

        //Add the queue time of all songs currently in the queue
        for(AudioTrack t: queue)
            count += t.getDuration();

        count += (player.getPlayingTrack().getDuration() - player.getPlayingTrack().getPosition());
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(count),
                TimeUnit.MILLISECONDS.toMinutes(count) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(count) % TimeUnit.MINUTES.toSeconds(1));
    }

    /**
     * Skips to desired index
     * @param idx
     */
    public void skip(int idx) {
        for(int i = 0; i<idx-1; i++) {
            queue.poll();
        }
        player.getPlayingTrack().stop();
        nextTrack();
    }

    public void queueTop(AudioTrack track) {
        if(!player.startTrack(track, true)) {
            ArrayList<AudioTrack> trackList = new ArrayList<>(queue);

            trackList.add(0, track);

            queue.clear();
            queue.addAll(trackList);
            trackList.clear();
        }
    }

    /**
     * Gets the index of a given song (starts at 1 for user purposes)
     * @param at The AudioTrack to be queued
     * @return
     */
    public int getSongIndex(AudioTrack at) {
        int count = 1;
        for(AudioTrack track: queue) {
            if(track.equals(at))
                break;
            count++;
        }
        return count;
    }

    /**
     * Moves a song from one index to another
     * @param idx1 The song's current index
     * @param idx2 The desired index
     */
    public void move(int idx1, int idx2) {
        ArrayList<AudioTrack> trackList = getArrayList();
        try {
            if(idx1 == idx2)
                return;

            AudioTrack subjectTrack = trackList.get(idx1);
            if(idx1 > idx2) {
                trackList.remove(idx1); //Remove the track from index (remember the number the user knows is 1 more than real index)
                trackList.add(idx2, subjectTrack); //Add that track again but at the new index
            }
            else {
                trackList.add(idx2+1, subjectTrack); //Add that track again but at the new index
                trackList.remove(idx1); //Remove the track from index (remember the number the user knows is 1 more than real index)
            }
            replaceQueue(trackList);
        } catch(NullPointerException ignored) {}
    }

    /**
     * Gets the total time until an audio track that has been added to the end of the queue
     * @param at
     * @return
     */
    public String getTotalTimeTil(AudioTrack at) {
        int idx = getSongIndex(at)-1;
        long totalTime = 0;
        ArrayList<AudioTrack> trackList = getArrayList();
        for(int i = 0; i<idx; i++) {
            totalTime += trackList.get(i).getDuration();
        }
        totalTime += (player.getPlayingTrack().getDuration() - player.getPlayingTrack().getPosition());

        return CmdUtils.longToHHMMSS(totalTime);
    }

    /**
     * Shuffles the queue
     */
    public void shuffle() {
        //Convert the queue to arraylist
        ArrayList<AudioTrack> trackList = new ArrayList<>(queue);

        Collections.shuffle(trackList);

        queue.clear(); //Clear the queue
        queue.addAll(trackList); //Repopulate with array list elements
        trackList.clear(); //Now clear the arraylist to save memory
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    /**
     * Returns the looping status of the track scheduler
     * @return
     */
    public boolean isLooping() {
        return looping;
    }

    /**
     * Changes the looping status of the track scheduler
     * @param loop
     */
    public void setLooping(boolean loop) {
        looping = loop;
    }

}
