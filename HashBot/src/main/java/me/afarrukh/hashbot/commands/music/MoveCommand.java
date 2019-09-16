package me.afarrukh.hashbot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MoveCommand extends Command implements MusicCommand {
    public MoveCommand() {
        super("move");
        addAlias("m");
        description = "Moves a track from one index on the list to another";
        addParameter("original index", "The current position of the track you would like to move");
        addParameter("new index", "The new position of the track you would like to move");
        setExampleUsage("move 17 2");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        try {
            String[] tokens = params.split(" ");
            if (MusicUtils.canInteract(evt)) {
                int oldPos = Integer.parseInt(tokens[0]) - 1;
                int newPos = Integer.parseInt(tokens[1]) - 1;

                if (oldPos < 0 || newPos + 1 > Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().getArrayList().size()) {
                    evt.getChannel().sendMessage("Invalid index.").queue();
                    return;
                }
                if (newPos < 0 || oldPos + 1 > Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().getArrayList().size()) {
                    evt.getChannel().sendMessage("Invalid index.").queue();
                    return;
                }
                AudioTrack track = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().getArrayList().get(oldPos);
                Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().move(oldPos, newPos);
                evt.getChannel().sendMessage("Moved `" + track.getInfo().title + "` to position " + tokens[1]).queue();
            }
        } catch (NullPointerException e) {
            onIncorrectParams(evt.getTextChannel());
        } catch (NumberFormatException e) {
            evt.getChannel().sendMessage("Please enter numerical indices only.").queue();
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: move <old position> <new position>").queue();
    }
}
