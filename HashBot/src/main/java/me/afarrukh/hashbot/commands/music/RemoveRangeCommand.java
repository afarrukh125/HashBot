package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Abdullah
 * Created on 17/09/2019 at 10:21
 */
public class RemoveRangeCommand extends Command implements MusicCommand {

    public RemoveRangeCommand() {
        super("removerange");
        addAlias("rmrange");
        addAlias("rr");
        description = "Removes all tracks between the start and end (inclusive) ranges from the track queue";
        addParameter("ranges", "Comma separated ranges to remove from the track queue, or individual indexes. " +
                "Ranges should be specified with dashes. You can also provide a single number to remove all tracks from that index onwards.");

        addExampleUsage("removerange 23-36, 79-124, 89");
        addExampleUsage("removerange 28");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(params == null)
            onIncorrectParams(evt.getTextChannel());
        else {
            StringBuilder errorMessageBuilder = new StringBuilder();
            // Get all the track ranges provided
            String[] ranges = params.split(",");
//            for(String range: ranges) {
//                if(range.split("-").length==0)
//            }
        }
    }
}
