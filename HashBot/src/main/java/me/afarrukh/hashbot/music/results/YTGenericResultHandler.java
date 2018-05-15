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
    boolean playTop;
    String query;

    public YTGenericResultHandler(GuildMusicManager gmm, MessageReceivedEvent evt, boolean playTop) {
        this.gmm = gmm;
        this.evt = evt;
        this.playTop = playTop;
        String[] params = evt.getMessage().getContentRaw().split(" ", 2);
        this.query = params[1];

        if(!query.contains("list")) {
            evt.getChannel().sendMessage(":mag: **Searching**: `" +query+"`").queue();
        }

    }

    @Override
    public abstract void trackLoaded(AudioTrack audioTrack);

    @Override
    public abstract void playlistLoaded(AudioPlaylist audioPlaylist);

    @Override
    public abstract void noMatches();

    @Override
    public void loadFailed(FriendlyException e) {
        evt.getChannel().sendMessage("Nothing found by `" +query+"`").queue();
    }
}
