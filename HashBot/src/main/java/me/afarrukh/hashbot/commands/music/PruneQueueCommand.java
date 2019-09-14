package me.afarrukh.hashbot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class PruneQueueCommand extends Command implements MusicCommand {

    public PruneQueueCommand() {
        super("prunequeue");
        addAlias("pq");
        description = "Removes songs from any users that are no longer in voice.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        if (evt.getGuild().getAudioManager().getConnectedChannel() == null) {
            evt.getTextChannel().sendMessage("The bot is not connected to voice.").queue();
            return;
        }

        List<Member> memberList = evt.getGuild().getAudioManager().getConnectedChannel().getMembers();

        Queue<AudioTrack> trackQueue = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().getQueue();
        List<String> prunedUsers = new ArrayList<>();

        int removedCount = 0;

        for (AudioTrack track : trackQueue) {
            String trackOwner = (String) track.getUserData();
            boolean remove = false;

            for (int i = 0; i < memberList.size(); i++) {
                Member m = memberList.get(i);
                if (m.getUser().getName().equals(trackOwner))
                    break;
                if (i == memberList.size() - 1 && !m.getUser().getName().equalsIgnoreCase(trackOwner)) {
                    trackQueue.remove(track);
                    removedCount++;

                    if (!prunedUsers.contains(trackOwner))
                        prunedUsers.add(trackOwner);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Removed ").append(removedCount).append(" songs from the queue.\n");
        if (!prunedUsers.isEmpty()) {
            sb.append("From user(s): \n");
            for (String s : prunedUsers)
                sb.append(s).append("\n");
        }
        evt.getTextChannel().sendMessage(sb.toString().trim()).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
