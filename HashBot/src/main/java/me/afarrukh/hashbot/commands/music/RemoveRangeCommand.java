package me.afarrukh.hashbot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Abdullah
 * Created on 17/09/2019 at 10:21
 * <p>
 * Removes a range of tracks from the track queue, ultimately an extension of <code>RemoveCommand</code>
 * @see RemoveCommand
 */
public class RemoveRangeCommand extends Command implements MusicCommand {

    public RemoveRangeCommand() {
        super("removerange");
        addAlias("rmrange");
        addAlias("rr");
        description = "Removes all tracks between the start and end (both inclusive) ranges from the track queue";
        addParameter("ranges", "Comma separated ranges to remove from the track queue, or individual indexes. " +
                "Ranges should be specified with dashes. You can also provide a single number to remove all tracks from that index onwards.");

        addExampleUsage("removerange 23-36, 79-124");
        addExampleUsage("removerange 28");
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        if (params == null)
            onIncorrectParams(evt.getChannel());
        else {
            StringBuilder errorMessageBuilder = new StringBuilder();
            // Get all the track ranges provided
            String[] ranges = params.split(",");

            List<AudioTrack> trackList = new ArrayList<>(Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().getArrayList());
            List<List<AudioTrack>> trackRanges = new ArrayList<>();
            for (String range : ranges) {
                range = range.trim();
                // Now processing the individual ranges provided
                String[] rangeEndPoints = range.split("-");
                // This assumes they want to remove all tracks from this index onwards only
                if (rangeEndPoints.length == 1 && (params.split(" ").length == 1)) {
                    int indivRange = Integer.parseInt(rangeEndPoints[0]);
                    if (indivRange > trackList.size() || indivRange < 0) {
                        errorMessageBuilder.append("Range ").append(indivRange)
                                .append(" was invalid, as it is outside of the track queue size").append("\n");
                        break;
                    }
                    try {
                        trackRanges.add(new ArrayList<>(trackList.subList(Integer.parseInt(rangeEndPoints[0]) - 1, trackList.size())));
                    } catch (NumberFormatException e) {
                        errorMessageBuilder.append("- Range ").append(range).append(" was invalid. Please enter numeric ranges only\n");
                        break;
                    } catch (IndexOutOfBoundsException e) {
                        errorMessageBuilder.append("- Range ").append(range).append(" was invalid. Please enter numbers in a valid range\n");
                        break;
                    }
                } else {
                    // If it is not a range
                    if (range.split("-").length == 1) {
                        errorMessageBuilder.append("- Range ").append(range).append(" was invalid, as it is not a range. "
                                + "If you wish to remove individual tracks use ")
                                .append(Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPrefix())
                                .append(new RemoveCommand().getName()).append(".").append(" Alternatively you can " +
                                "provide a range of 0 (e.g. 87-87)\n");
                        continue;
                    }
                    // Parsing ranges
                    int range1 = Integer.parseInt(range.split("-")[0].trim());
                    int range2 = Integer.parseInt(range.split("-")[1].trim());

                    // Checking that both ranges are valid
                    if (range2 < range1) {
                        errorMessageBuilder.append("- Range ").append(range.trim())
                                .append(" was invalid. Ensure the first index is ").append("smaller than the second.");
                        continue;
                    }
                    // If the ranges are less than 0 or greater than trackList size, then we have an error. Skip this.
                    if (range1 < 0 || range2 < 0 || range1 > trackList.size() || range2 > trackList.size()) {
                        errorMessageBuilder.append("- Range ").append(range.trim())
                                .append(" was invalid. Ensure you are entering values within queue size.").append("\n");
                        continue;
                    }

                    // Add the track ranges to the list of tracks to be removed
                    trackRanges.add(new ArrayList<>(trackList.subList(range1 - 1, range2)));
                }
            }
            int ct = 0;
            for (List<AudioTrack> tracks : trackRanges) {
                ct += tracks.size();
                trackList.removeAll(tracks);
            }
            // Replace the queue
            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().replaceQueue(trackList);

            if (ct == 0)
                evt.getChannel().sendMessage("Could not remove any tracks from the queues. Check the errors below to see what went wrong")
                        .queue();
            else
                // Inform them of how many tracks were removed
                evt.getChannel().sendMessage("Removed " + ct + " tracks from the queue").queue();


            // If there are error messages for any of the ranges then inform the invoking user
            if (!errorMessageBuilder.toString().isEmpty())
                evt.getChannel().sendMessage(new StringBuilder("```Errors: ").append("\n").append(errorMessageBuilder).append("```"))
                        .queue();

        }
    }
}
