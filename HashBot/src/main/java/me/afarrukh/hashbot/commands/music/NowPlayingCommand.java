package me.afarrukh.hashbot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class NowPlayingCommand extends Command implements MusicCommand {
    public NowPlayingCommand() {
        super("nowplaying");
        addAlias("current");
        addAlias("np");
        description = "Shows the currently playing track";
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        try {
            AudioTrack currentTrack = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().getPlayingTrack();
            evt.getChannel().sendMessage(EmbedUtils.getSingleTrackEmbed(currentTrack, evt)).queue();
        } catch (NullPointerException e) {
            evt.getChannel().sendMessage(EmbedUtils.getNothingPlayingEmbed()).queue();
        }
    }
}
