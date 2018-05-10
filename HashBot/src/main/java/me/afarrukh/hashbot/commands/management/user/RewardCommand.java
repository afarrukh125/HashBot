package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.utils.CmdUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RewardCommand extends Command {

    public RewardCommand() {
        super("addCredit");
    }
    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        String[] tokens = params.split(" ");
        String targetUser = CmdUtils.getParamsAsString(tokens, 0, tokens.length-2);
        int amt = 0;

        try {
            amt = Integer.parseInt(tokens[tokens.length-1]);
        } catch(NumberFormatException e) { onIncorrectParams(evt.getTextChannel()); }

        for(Member m: evt.getGuild().getMembers()) {
            if(m.getUser().getName().equalsIgnoreCase(targetUser)) {
                Invoker inv = new Invoker(m);
                inv.addCredit(amt);
                evt.getTextChannel().sendMessage("Rewarded " +m.getUser().getName()+ " with " +amt+ " credit.").queue();
                return;
            }
        }
        evt.getTextChannel().sendMessage("User not found.").queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        System.out.println("Incorrect amount given.");
    }
}
