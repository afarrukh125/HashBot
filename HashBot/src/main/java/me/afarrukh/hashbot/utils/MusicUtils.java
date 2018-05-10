package me.afarrukh.hashbot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MusicUtils {
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
     * @param evt
     * @return A true or false value depending on whether or not the user can interact with the bot
     */
    public static boolean canInteract(MessageReceivedEvent evt) {
        try {
            String memberChannel = evt.getMember().getVoiceState().getAudioChannel().getId();
            String botChannel = evt.getGuild().getAudioManager().getConnectedChannel().getId();
            if(memberChannel.equals(botChannel))
                return true;
            evt.getChannel().sendMessage("You cannot interact with the bot unless you are in a voice channel.").queue();
            return false;
        }catch(NullPointerException e) {}
        return false;
    }

    /**
     * Gets the URL for the thumbnail for the provided track
     * @param at
     * @return A string with the URL to the given audio track
     */
    public static String getThumbnailURL(AudioTrack at) {
        String vidURL = at.getInfo().uri.toString();
        int idx = vidURL.indexOf("?v=");
        String imgURL = vidURL.substring(idx+3);
        return "https://img.youtube.com/vi/" +imgURL+ "/0.jpg";
    }

    /**
     * @param track
     * @return A string with the highlighted current position as the appropriate string in constants file
     */
    public static String getMusicBar(AudioTrack track) {
        long totalDuration = track.getDuration();
        long currentPosition = track.getPosition();

        int remainder = (int) Math.round((double) currentPosition/totalDuration*Constants.MUSICBAR_SCALE);
        String val = "";
        for(int i = 0; i<Constants.MUSICBAR_SCALE; i++) {
            if(i == remainder)
                val += ":"+Constants.SELECTEDPOS+":";
            else
                val += Constants.UNSELECTEDPOS;
        }
        return val;
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
        Bot.getGuildAudioPlayer(evt.getGuild()).getPlayer().setVolume(vol);
    }

    /**
     * Returns the current song and sends a message to the channel associated with the message received event, unused method
     * @deprecated
     * @param evt
     */
    public static void getCurrentSong(MessageReceivedEvent evt) {
        try {
            AudioTrack currentTrack = Bot.getGuildAudioPlayer(evt.getGuild()).getPlayer().getPlayingTrack();
            String message = "The currently playing song is `" +currentTrack
                    .getInfo().title+"`";
            int currentTime = (int) currentTrack.getPosition()/1000;
            int totalTime = (int) (currentTrack.getDuration()/1000);
            String times = String.format("%02d:%02d/%02d:%02d", currentTime/60, currentTime%60, totalTime/60, totalTime%60);
            message += "\n" + times;
            evt.getTextChannel().sendMessage(message).queue();
        } catch(NullPointerException e) {
            evt.getChannel().sendMessage("Nothing is playing right now.").queue();
        }
    }
}
