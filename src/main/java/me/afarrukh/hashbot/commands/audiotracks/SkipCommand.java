package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import me.afarrukh.hashbot.utils.EmbedUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SkipCommand extends Command implements AudioTrackCommand {

    public SkipCommand(Database database) {
        super("skip", database);
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
            var injector = Guice.createInjector(new CoreBotModule());
            var gmm = injector.getInstance(AudioTrackManager.class).getGuildAudioPlayer(evt.getGuild());
            if (gmm.getScheduler().getQueue().isEmpty() && (gmm.getPlayer().getPlayingTrack() == null)) return;
            gmm.getScheduler().setLooping(false);
            if (params == null) {
                evt.getChannel()
                        .sendMessageEmbeds(
                                EmbedUtils.getSkippedEmbed(gmm.getPlayer().getPlayingTrack()))
                        .queue();

                gmm.getPlayer().getPlayingTrack().stop();
                gmm.getScheduler().nextTrack();

                if (gmm.getPlayer().getPlayingTrack() != null)
                    evt.getChannel()
                            .sendMessageEmbeds(
                                    EmbedUtils.getSkippedToEmbed(gmm.getPlayer().getPlayingTrack()))
                            .queue();
            } else {
                try {
                    String[] tokens = params.split(" ");
                    int idx = Integer.parseInt(tokens[0]);
                    if (idx > gmm.getScheduler().getQueue().size() || idx <= 0) {
                        evt.getChannel()
                                .sendMessage("Cannot skip to that index, out of bounds.")
                                .queue();
                        return;
                    }
                    evt.getChannel()
                            .sendMessageEmbeds(
                                    EmbedUtils.getSkippedEmbed(gmm.getPlayer().getPlayingTrack()))
                            .queue();
                    gmm.getScheduler().skip(idx);
                    evt.getChannel()
                            .sendMessageEmbeds(
                                    EmbedUtils.getSkippedToEmbed(gmm.getPlayer().getPlayingTrack()))
                            .queue();
                } catch (NumberFormatException e) {
                    evt.getChannel().sendMessage("Please enter numbers only.").queue();
                } catch (NullPointerException e) {
                    evt.getChannel()
                            .sendMessage("Please enter only at most two parameters.")
                            .queue();
                }
            }
        }
    }
}
