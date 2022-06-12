package me.afarrukh.hashbot.commands.audiotracks;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import me.afarrukh.hashbot.utils.EmbedUtils;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SkipCommand extends Command implements AudioTrackCommand {

    public SkipCommand() {
        super("skip");
        addAlias("n");
        addAlias("next");
        addAlias("s");
        addAlias("fs");
        description = "Skips to the next track or desired position if position is provided";
        addParameter("position", "**Optional**: The position to skip to in the track queue");
        addExampleUsage("skip 28");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (AudioTrackUtils.canInteract(evt)) {
            GuildAudioTrackManager gmm = Bot.trackManager.getGuildAudioPlayer(evt.getGuild());
            if (gmm.getScheduler().getQueue().isEmpty() && (gmm.getPlayer().getPlayingTrack() == null))
                return;
            gmm.getScheduler().setLooping(false);
            if (params == null) {
                evt.getChannel().sendMessageEmbeds(EmbedUtils.getSkippedEmbed(gmm.getPlayer().getPlayingTrack())).queue();

                gmm.getPlayer().getPlayingTrack().stop();
                gmm.getScheduler().nextTrack();

                if (gmm.getPlayer().getPlayingTrack() != null)
                    evt.getChannel().sendMessageEmbeds(EmbedUtils.getSkippedToEmbed(gmm.getPlayer().getPlayingTrack(), evt)).queue();
            } else {
                try {
                    String[] tokens = params.split(" ");
                    int idx = Integer.parseInt(tokens[0]);
                    if (idx > gmm.getScheduler().getQueue().size() || idx <= 0) {
                        evt.getChannel().sendMessage("Cannot skip to that index, out of bounds.").queue();
                        return;
                    }
                    evt.getChannel().sendMessageEmbeds(EmbedUtils.getSkippedEmbed(gmm.getPlayer().getPlayingTrack())).queue();
                    gmm.getScheduler().skip(idx);
                    evt.getChannel().sendMessageEmbeds(EmbedUtils.getSkippedToEmbed(gmm.getPlayer().getPlayingTrack(), evt)).queue();
                } catch (NumberFormatException e) {
                    evt.getChannel().sendMessage("Please enter numbers only.").queue();
                } catch (NullPointerException e) {
                    evt.getChannel().sendMessage("Please enter only at most two parameters.").queue();
                }
            }
        }
    }
}
