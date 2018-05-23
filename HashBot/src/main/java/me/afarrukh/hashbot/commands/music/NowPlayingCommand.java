package me.afarrukh.hashbot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class NowPlayingCommand extends Command {
    public NowPlayingCommand() {
        super("current", new String[]{"np"});
        description = "Shows the currently playing song";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        try {
            AudioTrack currentTrack = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().getPlayingTrack();
            evt.getChannel().sendMessage(EmbedUtils.getSingleSongEmbed(currentTrack, evt)).queue();
        } catch(NullPointerException e) {
            evt.getChannel().sendMessage(EmbedUtils.getNothingPlayingEmbed()).queue();
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
