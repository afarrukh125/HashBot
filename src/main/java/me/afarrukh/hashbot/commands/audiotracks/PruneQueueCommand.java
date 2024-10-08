package me.afarrukh.hashbot.commands.audiotracks;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PruneQueueCommand extends Command implements AudioTrackCommand {

    private final AudioTrackManager audioTrackManager;

    public PruneQueueCommand(AudioTrackManager audioTrackManager) {
        super("prunequeue");
        this.audioTrackManager = audioTrackManager;
        addAlias("pq");
        description = "Removes tracks from any users that are no longer in voice.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        if (evt.getGuild().getAudioManager().getConnectedChannel() == null) {
            evt.getChannel().sendMessage("The bot is not connected to voice.").queue();
            return;
        }

        List<Member> memberList =
                evt.getGuild().getAudioManager().getConnectedChannel().getMembers();

        Queue<AudioTrack> trackQueue = audioTrackManager
                .getGuildAudioPlayer(evt.getGuild())
                .getScheduler()
                .getQueue();
        List<String> prunedUsers = new ArrayList<>();

        int removedCount = 0;

        for (AudioTrack track : trackQueue) {
            String trackOwner = (String) track.getUserData();

            for (int i = 0; i < memberList.size(); i++) {
                Member m = memberList.get(i);
                if (m.getUser().getName().equals(trackOwner)) break;
                if (i == memberList.size() - 1 && !m.getUser().getName().equalsIgnoreCase(trackOwner)) {
                    trackQueue.remove(track);
                    removedCount++;

                    if (!prunedUsers.contains(trackOwner)) prunedUsers.add(trackOwner);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Removed ").append(removedCount).append(" tracks from the queue.\n");
        if (!prunedUsers.isEmpty()) {
            sb.append("From user(s): \n");
            for (String s : prunedUsers) sb.append(s).append("\n");
        }
        evt.getChannel().sendMessage(sb.toString().trim()).queue();
    }
}
