package me.afarrukh.hashbot.commands.management.guild;

import me.afarrukh.hashbot.commands.AdminCommand;
import me.afarrukh.hashbot.commands.Command;
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

        description = "Set how many reactions are required on a message to pin it.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        try {
            Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).setPinThreshold(Integer.parseInt(params));
        } catch(NumberFormatException e) {
            evt.getTextChannel().sendMessage("Please enter a numerical value for threshold. The threshold is the number of reactions needed for a message to be pinned.")
                    .queue();
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}


