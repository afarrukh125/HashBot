package me.afarrukh.hashbot.commands.management.guild;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by Abdullah on 10/04/2019 16:10
 */
public class SetPinThresholdCommand extends Command implements AdminCommand {

    public SetPinThresholdCommand() {
        super("pinthreshold");
        addAlias("threshold");
        addAlias("pt");
        addAlias("setthreshold");
        addAlias("setpinthreshold");

        description = "Set how many reactions are required on a message to pin it. The threshold is the number of reactions needed for a message to be pinned.";
        addParameter("threshold", "The number of reactions that are required for a message to be added to the pinned channel");


    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        try {
            int newValue = Integer.parseInt(params);
            if (newValue <= 0) {
                evt.getTextChannel().sendMessage("The minimum pin threshold value is 1").queue();
                return;
            }
            Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).setPinThreshold(newValue);
            evt.getTextChannel().sendMessage("The pinned threshold is now " + Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPinThreshold()).queue();
        } catch (NumberFormatException e) {
            evt.getTextChannel().sendMessage("Please enter a numerical value for threshold. The threshold is the number of reactions needed for a message to be pinned.")
                    .queue();
        }
    }
}


