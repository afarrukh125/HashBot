package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.data.Database;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class PruneQueueCommand extends Command implements AudioTrackCommand {

    public PruneQueueCommand(Database database) {
        super("prunequeue", database);
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

        var injector = Guice.createInjector(new CoreBotModule());

        var trackQueue = injector.getInstance(Bot.class)
                .getTrackManager()
                .getGuildAudioPlayer(evt.getGuild())
                .getScheduler()
                .getQueue();
        List<String> prunedUsers = new ArrayList<>();

        int removedCount = 0;

        for (var track : trackQueue) {
            var trackOwner = (String) track.getUserData();

            for (int i = 0; i < memberList.size(); i++) {
                var m = memberList.get(i);
                if (m.getUser().getName().equals(trackOwner)) break;
                if (i == memberList.size() - 1 && !m.getUser().getName().equalsIgnoreCase(trackOwner)) {
                    trackQueue.remove(track);
                    removedCount++;

                    if (!prunedUsers.contains(trackOwner)) prunedUsers.add(trackOwner);
                }
            }
        }
        var sb = new StringBuilder();
        sb.append("Removed ").append(removedCount).append(" tracks from the queue.\n");
        if (!prunedUsers.isEmpty()) {
            sb.append("From user(s): \n");
            for (var s : prunedUsers) sb.append(s).append("\n");
        }
        evt.getChannel().sendMessage(sb.toString().trim()).queue();
    }
}
