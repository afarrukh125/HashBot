package me.afarrukh.hashbot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PruneQueueCommand extends Command {

    public PruneQueueCommand() {
        super("prunequeue", new String[]{"pq"});
        description = "Removes songs from any users that are no longer in voice";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        VoiceChannel voiceChannel = evt.getGuild().getAudioManager().getConnectedChannel();

        for(AudioTrack track: Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().getQueue()) {
            String trackOwner = (String) track.getUserData();
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
