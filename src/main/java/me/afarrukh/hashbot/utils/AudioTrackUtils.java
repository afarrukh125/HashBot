package me.afarrukh.hashbot.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.concurrent.BlockingQueue;

public class AudioTrackUtils {

    public static void play(
            MessageReceivedEvent evt, GuildAudioTrackManager trackManager, AudioTrack track, boolean playTop) {

        connectToChannel(evt.getMember());
        if (playTop) {
            trackManager.getScheduler().queueTop(track);
        } else {
            trackManager.getScheduler().queue(track);
        }

        if (playTop) {
            evt.getChannel()
                    .sendMessageEmbeds(EmbedUtils.getQueuedTopEmbed(trackManager, track))
                    .queue();
        } else
            evt.getChannel()
                    .sendMessageEmbeds(EmbedUtils.getQueuedEmbed(trackManager, track, evt))
                    .queue();
    }

    public static void connectToChannel(Member caller) {
        if (!caller.getGuild().getAudioManager().isConnected()) {
            if (caller.getVoiceState().inAudioChannel()) {
                AudioManager audioManager = caller.getGuild().getAudioManager();
                audioManager.openAudioConnection(caller.getVoiceState().getChannel());
            }
        }
    }

    public static void disconnect(Guild guild) {
        GuildAudioTrackManager gm = Bot.trackManager.getGuildAudioPlayer(guild);
        if (gm.getPlayer().getPlayingTrack() != null) {
            gm.getPlayer().getPlayingTrack().stop();
        } else {
            // TODO hack since if there is a track running it will also cause bot user to disconnect which also
            // schedules a timer that has been cancelled
            gm.getDisconnectTimer().cancel();
        }
        gm.getScheduler().getQueue().clear();
        gm.getScheduler().setLoopingQueue(false);
        gm.getScheduler().setLooping(false);
        gm.getPlayer().setPaused(false);
        guild.getAudioManager().closeAudioConnection();
        gm.getPlayer().destroy();
    }

    public static String getPlaylistDuration(AudioPlaylist pl) {
        long duration = 0;
        for (AudioTrack t : pl.getTracks()) {
            duration += t.getDuration();
        }

        return CmdUtils.longToHHMMSS(duration);
    }

    public static boolean canInteract(MessageReceivedEvent evt) {
        if (evt.getGuild()
                                .getMemberById(Bot.botUser().getSelfUser().getId())
                                .getVoiceState()
                                .getChannel()
                        == null
                || evt.getMember().getVoiceState().getChannel() == null) {
            return false;
        }

        return evt.getGuild()
                .getMemberById(Bot.botUser().getSelfUser().getId())
                .getVoiceState()
                .getChannel()
                .equals(evt.getMember().getVoiceState().getChannel());
    }

    public static String getThumbnailURL(AudioTrack at) {
        String vidURL = at.getInfo().uri;
        int idx = vidURL.indexOf("?v=");
        String imgURL = vidURL.substring(idx + 3);
        return "https://img.youtube.com/vi/" + imgURL + "/0.jpg";
    }

    public static String getAudioTrackBar(AudioTrack track) {
        long totalDuration = track.getDuration();
        long currentPosition = track.getPosition();

        int remainder = (int) Math.round((double) currentPosition / totalDuration * Constants.AudioTrackBAR_SCALE);
        StringBuilder val = new StringBuilder();
        for (int i = 0; i < Constants.AudioTrackBAR_SCALE; i++) {
            if (i == remainder) val.append(":" + Constants.SELECTEDPOS + ":");
            else val.append(Constants.UNSELECTEDPOS);
        }
        return val.toString();
    }

    public static void setVolume(MessageReceivedEvent evt, int vol) {
        if (vol < 0 || vol > Constants.MAX_VOL) {
            evt.getChannel()
                    .sendMessage("You cannot set the volume to that value")
                    .queue();
            return;
        }
        Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().setVolume(vol);
        evt.getChannel().sendMessage("Volume set to " + vol).queue();
    }

    public static void pause(MessageReceivedEvent evt) {
        AudioPlayer ap = Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer();
        ap.setPaused(true);
    }

    public static void resume(MessageReceivedEvent evt) {
        AudioPlayer ap = Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer();
        ap.setPaused(false);
    }

    public static void seek(MessageReceivedEvent evt, int seconds) {
        try {
            AudioTrack track = Bot.trackManager
                    .getGuildAudioPlayer(evt.getGuild())
                    .getPlayer()
                    .getPlayingTrack();

            if (seconds > track.getDuration() / 1000 || seconds < 0)
                evt.getChannel()
                        .sendMessage("Please enter a valid time for this track to seek. "
                                + "Maximum time in seconds for this track is " + ((track.getDuration() / 1000) - 1)
                                + " seconds.")
                        .queue();
            else {
                int toMilliSeconds = seconds * 1000;
                track.setPosition(toMilliSeconds);
                evt.getChannel()
                        .sendMessage("Set position of current track to " + CmdUtils.longToMMSS(seconds))
                        .queue();
            }
        } catch (NullPointerException e) {
            evt.getChannel().sendMessage("Nothing is playing right now.").queue();
        }
    }

    public static void remove(MessageReceivedEvent evt, int idx) {
        BlockingQueue<AudioTrack> tracks = Bot.trackManager
                .getGuildAudioPlayer(evt.getGuild())
                .getScheduler()
                .getQueue();

        int count = 1;
        for (AudioTrack track : tracks) {
            if (count == idx) {
                tracks.remove(track);
                evt.getChannel()
                        .sendMessage("Removed `" + track.getInfo().title + "` from queue")
                        .queue();
                return;
            }
            count++;
        }
        evt.getChannel().sendMessage("Could not find a track at that index.").queue();
    }
}
