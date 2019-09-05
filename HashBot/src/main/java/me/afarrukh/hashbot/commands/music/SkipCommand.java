package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.music.GuildMusicManager;
import me.afarrukh.hashbot.utils.EmbedUtils;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SkipCommand extends Command implements MusicCommand {

    public SkipCommand() {
        super("skip");
        addAlias("n");
        addAlias("next");
        addAlias("s");
        description = "Skips to the next song or desired position if position is provided";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(MusicUtils.canInteract(evt)) {
            GuildMusicManager gmm = Bot.musicManager.getGuildAudioPlayer(evt.getGuild());
            if(gmm.getScheduler().getQueue().isEmpty() && (gmm.getPlayer().getPlayingTrack() == null))
                return;
            gmm.getScheduler().setLooping(false);
            if(params == null) {
                evt.getChannel().sendMessage(EmbedUtils.getSkippedEmbed(gmm.getPlayer().getPlayingTrack())).queue();

                gmm.getPlayer().getPlayingTrack().stop();
                gmm.getScheduler().nextTrack();

                if(gmm.getPlayer().getPlayingTrack() != null)
                    evt.getChannel().sendMessage(EmbedUtils.getSkippedToEmbed(gmm.getPlayer().getPlayingTrack(), evt)).queue();
            } else {
                try {
                    String[] tokens = params.split(" ");
                    int idx = Integer.parseInt(tokens[0]);
                    if(idx > gmm.getScheduler().getQueue().size() || idx <= 0) {
                        evt.getChannel().sendMessage("Cannot skip to that index, out of bounds.").queue();
                        return;
                    }
                    evt.getChannel().sendMessage(EmbedUtils.getSkippedEmbed(gmm.getPlayer().getPlayingTrack())).queue();
                    gmm.getScheduler().skip(idx);
                    evt.getChannel().sendMessage(EmbedUtils.getSkippedToEmbed(gmm.getPlayer().getPlayingTrack(), evt)).queue();
                } catch(NumberFormatException e) {evt.getChannel().sendMessage("Please enter numbers only.").queue(); }
                catch(NullPointerException e) {evt.getChannel().sendMessage("Please enter only at most two parameters.").queue(); }
            }
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
