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
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.concurrent.BlockingQueue;

public class MusicUtils {

    /**
     * @param evt          The message event used to retrieve data such as the channel the message is being sent to
     * @param musicManager The music manager to be queried
     * @param track        The track being queued
     * @param playTop      Whether or not the track is to be queued to the top of the list
     */
    public static void play(GuildMessageReceivedEvent evt, GuildMusicManager musicManager, AudioTrack track, boolean playTop) {

        connectToChannel(evt.getMember());
        Invoker invoker = Invoker.of(evt.getMember());
        if (playTop) {
            invoker.addCredit(-Constants.PLAY_TOP_COST);
            musicManager.getScheduler().queueTop(track);
        } else {
            musicManager.getScheduler().queue(track);
        }

        if (playTop) {
            evt.getChannel().sendMessage(EmbedUtils.getQueuedTopEmbed(musicManager, track, evt)).queue();
        } else
            evt.getChannel().sendMessage(EmbedUtils.getQueuedEmbed(musicManager, track, evt)).queue();
    }

    /**
     * Connects to a voice channel
     *
     * @param caller The user who called the command that caused the bot to connect
     */
    public static void connectToChannel(Member caller) {
        if (!caller.getGuild().getAudioManager().isConnected()) {
            if (caller.getVoiceState().inVoiceChannel()) {
                AudioManager audioManager = caller.getGuild().getAudioManager();
                audioManager.openAudioConnection(caller.getVoiceState().getChannel());
            }
        }
    }

    /**
     * Disconnects the bot from the channel provided in the provided message event
     *
     * @param guild The guild to disconnect the bot from
     */
    public static void disconnect(Guild guild) {
        GuildMusicManager gm = Bot.musicManager.getGuildAudioPlayer(guild);
        if (gm.getPlayer().getPlayingTrack() != null)
            gm.getPlayer().getPlayingTrack().stop();
        gm.getScheduler().getQueue().clear();
        gm.getScheduler().setLoopingQueue(false);
        gm.getScheduler().setLooping(false);
        gm.getPlayer().setPaused(false);
        gm.getDisconnectTimer().cancel();
        guild.getAudioManager().closeAudioConnection();
        gm.getPlayer().destroy();
        System.gc();
    }

    /**
     * Returns the duration of a playlist
     *
     * @param pl The playlist to be queried
     * @return A string with the duration of the playlist in HHMMSS format
     */
    public static String getPlaylistDuration(AudioPlaylist pl) {
        long duration = 0;
        for (AudioTrack t : pl.getTracks())
            duration += t.getDuration();

        return CmdUtils.longToHHMMSS(duration);
    }

    /**
     * Deletes the last bot message and message event message for 'play' commands mainly
     */
    public static void cleanPlayMessage(GuildMessageReceivedEvent evt) {
        BotUtils.deleteLastMsg(evt);
        evt.getMessage().delete().queue();
    }

    /**
     * @param evt The event associated with the call
     * @return True or false depending on whether music commands can be called
     */
    public static boolean canInteract(GuildMessageReceivedEvent evt) {
        if (evt.getGuild().getMemberById(Bot.botUser.getSelfUser().getId()).getVoiceState().getChannel() == null || evt.getMember().getVoiceState().getChannel() == null)
            return false;

        return evt.getGuild().getMemberById(Bot.botUser.getSelfUser().getId()).getVoiceState().getChannel()
                .equals(evt.getMember().getVoiceState().getChannel());
    }

    /**
     * Gets the URL for the thumbnail for the provided track
     *
     * @param at The audio track for which the thumbnail URl is to be found
     * @return A string with the URL to the given audio track
     */
    public static String getThumbnailURL(AudioTrack at) {
        String vidURL = at.getInfo().uri;
        int idx = vidURL.indexOf("?v=");
        String imgURL = vidURL.substring(idx + 3);
        return "https://img.youtube.com/vi/" + imgURL + "/0.jpg";
    }

    /**
     * @param track The audio track for which the current position and total duration is to be found
     * @return A string with the highlighted current position as the appropriate string in constants file
     */
    public static String getMusicBar(AudioTrack track) {
        long totalDuration = track.getDuration();
        long currentPosition = track.getPosition();

        int remainder = (int) Math.round((double) currentPosition / totalDuration * Constants.MUSICBAR_SCALE);
        StringBuilder val = new StringBuilder();
        for (int i = 0; i < Constants.MUSICBAR_SCALE; i++) {
            if (i == remainder)
                val.append(":" + Constants.SELECTEDPOS + ":");
            else
                val.append(Constants.UNSELECTEDPOS);
        }
        return val.toString();
    }

    /**
     * Changes the volume to a desired value
     *
     * @param evt The event being queried for information such as the channel
     * @param vol The volume to be changed to
     */
    public static void setVolume(GuildMessageReceivedEvent evt, int vol) {
        if (vol < 0 || vol > Constants.MAX_VOL) {
            evt.getChannel().sendMessage("You cannot set the volume to that value").queue();
            return;
        }
        Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().setVolume(vol);
        evt.getChannel().sendMessage("Volume set to " + vol).queue();
    }

    /**
     * Pauses the bot's audio player
     *
     * @param evt The message received event associated with the pause request being sent
     */
    public static void pause(GuildMessageReceivedEvent evt) {
        AudioPlayer ap = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer();
        ap.setPaused(true);
    }

    /**
     * Resumes the bot
     *
     * @param evt The message receieved event relating to the resume request
     */
    public static void resume(GuildMessageReceivedEvent evt) {
        AudioPlayer ap = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer();
        ap.setPaused(false);
    }

    /**
     * Looks for a particular time in the track
     *
     * @param evt     The message received event containing information regarding the track which is to be seeked through
     * @param seconds The time in seconds to be searched for in the currently playing track
     */
    public static void seek(GuildMessageReceivedEvent evt, int seconds) {
        try {
            AudioTrack track = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().getPlayingTrack();

            if (seconds > track.getDuration() / 1000 || seconds < 0)
                evt.getChannel().sendMessage("Please enter a valid time for this track to seek. " +
                        "Maximum time in seconds for this track is " + ((track.getDuration() / 1000) - 1) + " seconds.").queue();
            else {
                int toMilliSeconds = seconds * 1000;
                track.setPosition(toMilliSeconds);
                evt.getChannel().sendMessage("Set position of current track to " + seconds).queue();
            }
        } catch (NullPointerException e) {
            evt.getChannel().sendMessage("Nothing is playing right now.").queue();
        }
    }

    /**
     * The track to be removed from the queue at the given index
     *
     * @param evt The message received event associated with the track to be removed
     * @param idx The current index of the track to be removed
     */
    public static void remove(GuildMessageReceivedEvent evt, int idx) {
        BlockingQueue<AudioTrack> tracks = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().getQueue();

        int count = 1;
        for (AudioTrack track : tracks) {
            if (count == idx) {
                tracks.remove(track);
                evt.getChannel().sendMessage("Removed `" + track.getInfo().title + "` from queue").queue();
                return;
            }
            count++;
        }
        evt.getChannel().sendMessage("Could not find a track at that index.").queue();
    }

}
