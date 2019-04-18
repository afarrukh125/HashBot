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
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Class that contains a queue of music to play onto the bot
 */
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private BlockingQueue<AudioTrack> queue;
    private boolean looping;
    private boolean loopingQueue;
    private boolean fairPlay;
    private final Guild guild;

    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.guild = guild;
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        looping = false;
        fairPlay = false;
    }

    /**
     * Queues the song onto the track and informs if it was immediately played or queued
     * @param track The AudioTrack to play
     */
    public void queue(AudioTrack track) {
        if(!player.startTrack(track, true)) {
            queue.offer(track);
        }
        if(fairPlay)
            interleave(false);
    }

    public void nextTrack() {
        player.startTrack(queue.poll(), false);
    }

    /**
     * Decides what happens when the song ends
     * @param player The AudioPlayer associated with this trackscheduler
     * @param track The track that has been started
     */
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        Bot.musicManager.getGuildAudioPlayer(guild).resetDisconnectTimer();
    }

    /**
     * Decides what happens when a song ends
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the current one is finished and if the load failed

        // This is a temporary fix to our issue of the song not finishing TODO remove this
        if(endReason.name().equalsIgnoreCase("FINISHED") && (track.getPosition() != track.getDuration())) {

            System.out.println("TrackScheduler@onTrackEnd: Song ended at " + CmdUtils.longToMMSS(track.getPosition()) + "/" +
                    CmdUtils.longToMMSS(track.getDuration()));

            System.out.println("TrackScheduler@onTrackEnd: Starting track from the early ended position");
            AudioTrack clone = track.makeClone();
            clone.setPosition(track.getPosition());
            player.startTrack(clone, false);
            return;
        }
        Bot.musicManager.getGuildAudioPlayer(guild).getDisconnectTimer()
                .schedule(new DisconnectTimer(guild), Constants.DISCONNECT_DELAY*1000);
        if(isLooping()) {
            player.stopTrack();
            AudioTrack cloneTrack = track.makeClone();
            cloneTrack.setUserData(track.getUserData());
            player.startTrack(cloneTrack, false);
            return;
        }

        if(isLoopingQueue() && !isLooping()) {
            player.stopTrack();
            AudioTrack cloneTrack = track.makeClone();
            cloneTrack.setUserData(track.getUserData());
            queue(cloneTrack);
        }

        if(endReason.mayStartNext)
            nextTrack();
    }

    /**
     * Replaces the current queue with a new one
     * @param list The list to replace the current queue with
     */
    private void replaceQueue(ArrayList<AudioTrack> list) {
        queue = new LinkedBlockingQueue<>(list);
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
     * @return Returns a string in format h:m:s of the total queue time
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
     * @param idx The index to skip to in the queue
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
     * @param at The AudioTrack for which the index is to be found
     * @return The index of the provided song
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
     * @param at The audio track for which the time is to be calculated
     * @return Returns a string in HHMMSS format corresponding to how long until the song is playing
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
        ArrayList<AudioTrack> trackList = getArrayList();

        Collections.shuffle(trackList);

        queue.clear(); //Clear the queue
        queue.addAll(trackList); //Repopulate with array list elements
    }

    public void interleave(boolean shuffle) {
        ArrayList<AudioTrack> trackArrayList = getArrayList();
        ArrayList<String> userNameList = new ArrayList<>();
        HashMap<String, BlockingQueue<AudioTrack>> trackMap = new HashMap<>(); //Maps a username to their tracks

        for(AudioTrack track: trackArrayList) {
            String userName = track.getUserData().toString();
            if(!userNameList.contains(userName))
                userNameList.add(userName);
            if(trackMap.get(userName) == null)
                trackMap.put(userName, new LinkedBlockingQueue<>());
            trackMap.get(userName).add(track);
        }

        if(userNameList.size() == 0)
            return;

        if(userNameList.size() == 1 && shuffle) {
            shuffle();
            return;
        }

        if(player.getPlayingTrack() != null) {
            AudioTrack currentTrack = player.getPlayingTrack();
            if(userNameList.get(0).equals(currentTrack.getUserData().toString())) {
                userNameList.remove(0);
                userNameList.add(player.getPlayingTrack().getUserData().toString());
            }
        }

        if(shuffle)
            Collections.shuffle(userNameList); //Shuffling the list of users to decide who goes first

        ArrayList<AudioTrack> newTrackList = new ArrayList<>();

        for(int i = 0; i<trackArrayList.size()/userNameList.size(); i++) {
            for(String s: userNameList) {
                if(trackMap.get(s).isEmpty())
                    continue;
                newTrackList.add(trackMap.get(s).poll());
            }
        }

        ArrayList<AudioTrack> shuffledTracks = new ArrayList<>();

        if(newTrackList.size() < trackArrayList.size()) {
            for(BlockingQueue<AudioTrack> queue: trackMap.values()) {
                if(!queue.isEmpty())
                    shuffledTracks.addAll(queue);
            }
        }

        if(shuffle)
            Collections.shuffle(shuffledTracks);

        newTrackList.addAll(shuffledTracks);
        replaceQueue(newTrackList);
    }

    public void fairShuffle() {
        shuffle();
        interleave(true);
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    /**
     * Returns the looping status of the track scheduler
     * @return a boolean corresponding as to whether or not the song is looping
     */
    public boolean isLooping() {
        return looping;
    }

    /**
     * Changes the looping status of the track scheduler
     * @param loop The boolean status to which the looping is set to
     */
    public void setLooping(boolean loop) {
        looping = loop;
    }

    public boolean isLoopingQueue() {
        return loopingQueue;
    }

    public void setLoopingQueue(boolean loopingQueue) {
        this.loopingQueue = loopingQueue;
    }

    /**
     * @return The guild associated with this track scheduler
     */
    public Guild getGuild() {
        return this.guild;
    }

    public boolean isFairPlay() {
        return fairPlay;
    }

    public void setFairPlay(boolean fairPlay) {
        this.fairPlay = fairPlay;
    }
}
