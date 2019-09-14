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
 * This aims to be the barrier
 */
public class PlaylistLoader {

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
        while (currentIndex != track.getPos())
            wait();

        tracks.add(track);
        currentIndex++;
        notifyAll();

        System.out.println(currentIndex);
        if(currentIndex == maxSize) {
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

    public int getMaxSize() {
        return maxSize;
    }

    public void queueTracks() throws PlaylistException {
        if(tracks.size() != maxSize) {
            throw new PlaylistException("You cannot obtain the tracks until the list has finished loading");
        }

        Bot.musicManager.getGuildAudioPlayer(member.getGuild()).getScheduler().queue(tracks);
    }
}
