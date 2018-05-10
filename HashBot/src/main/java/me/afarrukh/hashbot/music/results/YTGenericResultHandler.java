package me.afarrukh.hashbot.music.results;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.music.GuildMusicManager;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

public abstract class YTGenericResultHandler implements AudioLoadResultHandler {

    GuildMusicManager gmm;
    MessageReceivedEvent evt;

    public YTGenericResultHandler(GuildMusicManager gmm, MessageReceivedEvent evt) {
        this.gmm = gmm;
        this.evt = evt;
    }

    @Override
    public abstract void trackLoaded(AudioTrack audioTrack);

    @Override
    public abstract void playlistLoaded(AudioPlaylist audioPlaylist);

    @Override
    public abstract void noMatches();

    @Override
    public abstract void loadFailed(FriendlyException e);

    public static void play(MessageReceivedEvent evt, GuildMusicManager musicManager, AudioTrack track, boolean playTop) {
        connectToChannel(evt);
        if(playTop)
            musicManager.getScheduler().queueTop(track);
        else
            musicManager.getScheduler().queue(track);
        evt.getChannel().sendMessage(EmbedUtils.getSingleSongEmbed(track, evt)).queue();
    }

    /**
     * Connects to a voice channel
     * @param evt The event object used to retrieve the VoiceChannel to connect to through the Member object in the event
     */
    public static void connectToChannel(MessageReceivedEvent evt) {
        if(!evt.getGuild().getAudioManager().isConnected()) {
            Member m = evt.getMember();
            if(m.getVoiceState().inVoiceChannel()) {
                AudioManager audioManager = evt.getGuild().getAudioManager();
                audioManager.openAudioConnection(m.getVoiceState().getChannel());
            }
            else {
                evt.getChannel().sendMessage("You cannot call the bot if you are not in a voice channel.").queue();
            }
        }
    }

    /**
     * Disconnects the bot from the channel provided in the provided message event
     * @param evt
     */
    public static void disconnect(MessageReceivedEvent evt) {
        GuildMusicManager gm = Bot.getGuildAudioPlayer(evt.getGuild());
        if(gm.getPlayer().getPlayingTrack() != null)
            gm.getPlayer().getPlayingTrack().stop();
        gm.getScheduler().getQueue().clear();
        gm.getScheduler().setLooping(false);
        gm.getPlayer().setPaused(false);
        evt.getGuild().getAudioManager().closeAudioConnection();
        gm.getPlayer().destroy();
    }
}
