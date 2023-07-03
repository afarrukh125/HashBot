package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MoveCommand extends Command implements AudioTrackCommand {
    public MoveCommand() {
        super("move");
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
                var injector = Guice.createInjector(new CoreBotModule());
                int oldPos = Integer.parseInt(tokens[0]) - 1;
                int newPos = Integer.parseInt(tokens[1]) - 1;

                AudioTrackManager trackManager = injector.getInstance(AudioTrackManager.class);
                if (oldPos < 0
                        || newPos + 1
                                > trackManager
                                        .getGuildAudioPlayer(evt.getGuild())
                                        .getScheduler()
                                        .getAsArrayList()
                                        .size()) {
                    evt.getChannel().sendMessage("Invalid index.").queue();
                    return;
                }
                if (newPos < 0
                        || oldPos + 1
                                > trackManager
                                        .getGuildAudioPlayer(evt.getGuild())
                                        .getScheduler()
                                        .getAsArrayList()
                                        .size()) {
                    evt.getChannel().sendMessage("Invalid index.").queue();
                    return;
                }
                AudioTrack track = trackManager
                        .getGuildAudioPlayer(evt.getGuild())
                        .getScheduler()
                        .getAsArrayList()
                        .get(oldPos);
                trackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().move(oldPos, newPos);
                evt.getChannel()
                        .sendMessage("Moved `" + track.getInfo().title + "` to position " + tokens[1])
                        .queue();
            }
        } catch (NullPointerException e) {
            onIncorrectParams(evt.getChannel().asTextChannel());
        } catch (NumberFormatException e) {
            evt.getChannel().sendMessage("Please enter numerical indices only.").queue();
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: move <old position> <new position>").queue();
    }
}
