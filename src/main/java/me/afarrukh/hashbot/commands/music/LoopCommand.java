package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.music.TrackScheduler;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LoopCommand extends Command implements MusicCommand {

    public LoopCommand() {
        super("loop");
        description = "Loops the currently playing track. Will no longer loop if next track is skipped to";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (MusicUtils.canInteract(evt)) {
            TrackScheduler trackScheduler = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler();
            trackScheduler.setLooping(!trackScheduler.isLooping());
            String status;
            if (trackScheduler.isLooping())
                status = "Now";
            else
                status = "No longer";
            evt.getChannel().sendMessage(status + " looping: `"
                    + Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().getPlayingTrack().getInfo().title + "`").queue();
        }
    }
}
