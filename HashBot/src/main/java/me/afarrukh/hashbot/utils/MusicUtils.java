package me.afarrukh.hashbot.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.music.GuildMusicManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.concurrent.BlockingQueue;

public class MusicUtils {

    /**
     * @param evt The message event used to retrieve data such as the channel the message is being sent to
     * @param musicManager The music manager to be queried
     * @param track The track being queued
     * @param playTop Whether or not the song is to be queued to the top of the list
     */
    public static void play(MessageReceivedEvent evt, GuildMusicManager musicManager, AudioTrack track, boolean playTop) {
        connectToChannel(evt);
        Invoker invoker = new Invoker(evt.getMember());
        if(playTop) {
            invoker.addCredit(-Constants.PLAY_TOP_COST);
            musicManager.getScheduler().queueTop(track);
        }
        else {
            musicManager.getScheduler().queue(track);
        }

        if(playTop) {
            evt.getChannel().sendMessage(EmbedUtils.getQueuedTopEmbed(musicManager, track, evt)).queue();
        }
        else
            evt.getChannel().sendMessage(EmbedUtils.getQueuedEmbed(musicManager, track, evt)).queue();
    }

    /**
     * Connects to a voice channel
     * @param evt The event object used to retrieve the VoiceChannel to connect to through the Member object in the event
     */
    private static void connectToChannel(MessageReceivedEvent evt) {
        if(!evt.getGuild().getAudioManager().isConnected()) {
            Member m = evt.getMember();
            if(m.getVoiceState().inVoiceChannel()) {
                AudioManager audioManager = evt.getGuild().getAudioManager();
                audioManager.openAudioConnection(m.getVoiceState().getChannel());
            }
        }
    }

    /**
     * Disconnects the bot from the channel provided in the provided message event
     * @param guild The guild to disconnect the bot from
     */
    public static void disconnect(Guild guild) {
        GuildMusicManager gm = Bot.musicManager.getGuildAudioPlayer(guild);
        if(gm.getPlayer().getPlayingTrack() != null)
            gm.getPlayer().getPlayingTrack().stop();
        gm.getScheduler().getQueue().clear();
        gm.getScheduler().setLooping(false);
        gm.getPlayer().setPaused(false);
        guild.getAudioManager().closeAudioConnection();
        gm.getPlayer().destroy();
    }
    /**
     * Returns the duration of a playlist
     * @param pl The playlist to be queried
     * @return A string with the duration of the playlist in HHMMSS format
     */
    public static String getPlaylistDuration(AudioPlaylist pl) {
        long duration = 0;
        for(AudioTrack t: pl.getTracks())
            duration += t.getDuration();

        return CmdUtils.longToHHMMSS(duration);
    }

    /**
     * Checks if the bot can be interacted by a particular user (the user in the event). This depends upon whether or not the user is connected
     * into the same channel as the bot
     * @param evt The message receieved event containing information as to whether or not a member can interact
     * @return A true or false value depending on whether or not the user can interact with the bot
     */
    public static boolean canInteract(MessageReceivedEvent evt) {
        try {
            String memberChannel = evt.getMember().getVoiceState().getAudioChannel().getId();
            String botChannel = evt.getGuild().getAudioManager().getConnectedChannel().getId();
            if(memberChannel.equals(botChannel))
                return true;
            evt.getChannel().sendMessage("You cannot interact with the bot unless you are in its voice channel").queue();
            return false;
        }catch(NullPointerException ignored) {}
        return false;
    }

    /**
     * Deletes the last bot message and message event message for 'play' commands mainly
     */
    public static void cleanPlayMessage(MessageReceivedEvent evt) {
        BotUtils.deleteLastMsg(evt);
        evt.getMessage().delete().queue();
    }

    /**
     * Gets the URL for the thumbnail for the provided track
     * @param at The audio track for which the thumbnail URl is to be found
     * @return A string with the URL to the given audio track
     */
    public static String getThumbnailURL(AudioTrack at) {
        String vidURL = at.getInfo().uri;
        int idx = vidURL.indexOf("?v=");
        String imgURL = vidURL.substring(idx+3);
        return "https://img.youtube.com/vi/" +imgURL+ "/0.jpg";
    }

    /**
     * @param track The audio track for which the current position and total duration is to be found
     * @return A string with the highlighted current position as the appropriate string in constants file
     */
    public static String getMusicBar(AudioTrack track) {
        long totalDuration = track.getDuration();
        long currentPosition = track.getPosition();

        int remainder = (int) Math.round((double) currentPosition/totalDuration*Constants.MUSICBAR_SCALE);
        StringBuilder val = new StringBuilder();
        for(int i = 0; i<Constants.MUSICBAR_SCALE; i++) {
            if(i == remainder)
                val.append(":" + Constants.SELECTEDPOS + ":");
            else
                val.append(Constants.UNSELECTEDPOS);
        }
        return val.toString();
    }

    /**
     * Changes the volume to a desired value
     * @param evt The event being queried for information such as the channel
     * @param vol The volume to be changed to
     */
    public static void changeVolume(MessageReceivedEvent evt, int vol) {
        if(vol < 0 || vol > Constants.MAX_VOL) {
            evt.getChannel().sendMessage("You cannot set the volume to that value, you troll.").queue();
            return;
        }
        evt.getChannel().sendMessage("Volume set to " +vol).queue();
        Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().setVolume(vol);
    }

    /**
     * Pauses the bot's audio player
     * @param evt The message received event associated with the pause request being sent
     */
    public static void pause(MessageReceivedEvent evt) {
        AudioPlayer ap = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer();
        ap.setPaused(true);
    }

    /**
     * Resumes the bot
     * @param evt The message receieved event relating to the resume request
     */
    public static void resume(MessageReceivedEvent evt) {
        AudioPlayer ap = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer();
        ap.setPaused(false);
    }

    /**
     * Looks for a particular time in the song
     * @param evt The message received event containing information regarding the song which is to be seeked through
     * @param seconds The time in seconds to be searched for in the currently playing song
     */
    public static void seek(MessageReceivedEvent evt, int seconds) {
        try {
            AudioTrack track = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().getPlayingTrack();

            if(seconds > track.getDuration()/1000 || seconds < 0)
                evt.getChannel().sendMessage("Please enter a valid time for this song to seek. " +
                        "Maximum time in seconds for this song is " +((track.getDuration()/1000)-1)+ " seconds.").queue();
            else {
                int toMilliSeconds = seconds * 1000;
                track.setPosition(toMilliSeconds);
                evt.getChannel().sendMessage("Set position of current song to " +seconds).queue();
            }
        } catch(NullPointerException e) {
            evt.getChannel().sendMessage("Nothing is playing right now.").queue();
        }
    }

    /**
     * The song to be removed from the queue at the given index
     * @param evt The message received event associated with the song to be removed
     * @param idx The current index of the song to be removed
     */
    public static void remove(MessageReceivedEvent evt, int idx) {
        BlockingQueue<AudioTrack> tracks = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().getQueue();

        int count = 1;
        for(AudioTrack track : tracks) {
            if(count == idx) {
                tracks.remove(track);
                evt.getTextChannel().sendMessage("Removed `" +track.getInfo().title+ "` from queue").queue();
                return;
            }
            count++;
        }
        evt.getTextChannel().sendMessage("Could not find a song at that index.").queue();
    }

}
