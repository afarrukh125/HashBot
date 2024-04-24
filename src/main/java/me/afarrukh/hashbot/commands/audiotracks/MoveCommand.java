package me.afarrukh.hashbot.commands.audiotracks;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MoveCommand extends Command implements AudioTrackCommand {
    private final Database database;

    public MoveCommand(Database database) {
        super("move");
        this.database = database;
        addAlias("m");
        addAlias("mv");
        description = "Moves a track from one index on the list to another";
        addParameter("original index", "The current position of the track you would like to move");
        addParameter("new index", "The new position of the track you would like to move");
        addExampleUsage("move 17 2");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        try {
            String[] tokens = params.split(" ");
            if (tokens.length != 2) {
                evt.getChannel()
                        .sendMessage(
                                "Please decide the position to move from/to. Use the help command if you need help")
                        .queue();
                return;
            }
            if (AudioTrackUtils.canInteract(evt)) {
                int oldPos = Integer.parseInt(tokens[0]) - 1;
                int newPos = Integer.parseInt(tokens[1]) - 1;

                if (oldPos < 0
                        || newPos + 1
                                > Bot.trackManager
                                        .getGuildAudioPlayer(evt.getGuild())
                                        .getScheduler()
                                        .getAsArrayList()
                                        .size()) {
                    evt.getChannel().sendMessage("Invalid index.").queue();
                    return;
                }
                if (newPos < 0
                        || oldPos + 1
                                > Bot.trackManager
                                        .getGuildAudioPlayer(evt.getGuild())
                                        .getScheduler()
                                        .getAsArrayList()
                                        .size()) {
                    evt.getChannel().sendMessage("Invalid index.").queue();
                    return;
                }
                AudioTrack track = Bot.trackManager
                        .getGuildAudioPlayer(evt.getGuild())
                        .getScheduler()
                        .getAsArrayList()
                        .get(oldPos);
                Bot.trackManager
                        .getGuildAudioPlayer(evt.getGuild())
                        .getScheduler()
                        .move(oldPos, newPos);
                evt.getChannel()
                        .sendMessage("Moved `" + track.getInfo().title + "` to position " + tokens[1])
                        .queue();
            }
        } catch (NullPointerException e) {
            onIncorrectParams(database, evt.getChannel().asTextChannel());
        } catch (NumberFormatException e) {
            evt.getChannel().sendMessage("Please enter numerical indices only.").queue();
        }
    }

    @Override
    public void onIncorrectParams(Database database, TextChannel channel) {
        channel.sendMessage("Usage: move <old position> <new position>").queue();
    }
}
