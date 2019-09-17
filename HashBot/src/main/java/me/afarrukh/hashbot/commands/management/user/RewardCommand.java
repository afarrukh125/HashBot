package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.OwnerCommand;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.utils.CmdUtils;
import me.afarrukh.hashbot.utils.UserUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class RewardCommand extends Command implements OwnerCommand {

    public RewardCommand() {
        super("reward");
        description = "Rewards credit to a user";
        addParameter("user name", "The user name of the user to give credit to");
        addParameter("amount", "The amount of credit to give to the user");
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        if (UserUtils.isBotAdmin(evt.getAuthor())) {
            evt.getChannel().sendMessage("Insufficient permission").queue();
            return;
        }
        String[] tokens = params.split(" ");
        String targetUser = CmdUtils.getParamsAsString(tokens, 0, tokens.length - 2);
        long amt;

        try {
            amt = Long.parseLong(tokens[tokens.length - 1]);
        } catch (NumberFormatException e) {
            evt.getChannel().sendMessage("Could not add this amount of credit, exceeds " + Long.MAX_VALUE + ".")
                    .queue();
            return;
        }

        for (Member m : evt.getGuild().getMembers()) {
            if (m.getUser().getName().equalsIgnoreCase(targetUser)) {
                Invoker inv = new Invoker(m);
                inv.addCredit(amt);
                evt.getChannel().sendMessage("Rewarded " + m.getUser().getName() + " with " + amt + " credit.").queue();
                return;
            }
        }
        evt.getChannel().sendMessage("User not found.").queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: reward <discord user name> <amount>").queue();
    }
}
