package me.afarrukh.hashbot.commands.management.guild;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.data.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Created by Abdullah on 10/04/2019 16:10
 */
public class SetPinThresholdCommand extends Command implements AdminCommand {

    public SetPinThresholdCommand(Database database) {
        super("pinthreshold", database);
        addAlias("threshold");
        addAlias("pt");
        addAlias("setthreshold");
        addAlias("setpinthreshold");

        description =
                "Set how many reactions are required on a message to pin it. The threshold is the number of reactions needed for a message to be pinned.";
        addParameter(
                "threshold",
                "The number of reactions that are required for a message to be added to the pinned channel");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        try {
            int newValue = Integer.parseInt(params);
            if (newValue <= 0) {
                evt.getChannel()
                        .sendMessage("The minimum pin threshold value is 1")
                        .queue();
                return;
            }
            database.setPinThresholdForGuild(evt.getGuild().getId(), newValue);
            evt.getChannel()
                    .sendMessage("The pinned threshold is now " + newValue)
                    .queue();
        } catch (NumberFormatException e) {
            evt.getChannel()
                    .sendMessage(
                            "Please enter a numerical value for threshold. The threshold is the number of reactions needed for a message to be pinned.")
                    .queue();
        }
    }
}
