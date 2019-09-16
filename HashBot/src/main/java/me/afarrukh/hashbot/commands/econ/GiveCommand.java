package me.afarrukh.hashbot.commands.econ;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.EconCommand;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.utils.CmdUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class GiveCommand extends Command implements EconCommand {

    public GiveCommand() {
        super("give");
        addAlias("transfer");
        description = "Transfers credit to the mentioned user";
        addParameter("username", "The user who you would like to give credit to. This can be " +
                "their name, or you can simply mention them.");
        addParameter("amount", "The amount you would like to give to the user");

        setExampleUsage("give HashBot 5000");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        Member m = null;
        if (params == null) {
            onIncorrectParams(evt.getTextChannel());
            return;
        }
        if (evt.getMessage().getMentionedMembers().isEmpty()) {
            String[] userName = params.split(" ");
            String memberByName = CmdUtils.getParamsAsString(userName, 0, userName.length - 2);
            for (Member member : evt.getGuild().getMembers()) {
                if (member.getEffectiveName().equalsIgnoreCase(memberByName) || member.getUser().getName().equalsIgnoreCase(memberByName))
                    m = member;
            }

            if (m == null) {
                evt.getTextChannel().sendMessage("Invalid user provided.").queue();
                return;
            }
        } else
            m = evt.getMessage().getMentionedMembers().get(0);

        long amount;
        String[] tokens = params.split(" ");

        try {
            amount = Long.parseLong(tokens[tokens.length - 1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | NullPointerException e) {
            evt.getTextChannel().sendMessage("You must provide a numerical amount to give to the mentioned user.").queue();
            return;
        }

        if (amount <= 0) {
            evt.getTextChannel().sendMessage("You must provide an amount to transfer.").queue();
            return;
        }

        Invoker invoker = new Invoker(evt.getMember());
        Invoker receiver = new Invoker(m);

        if (amount > invoker.getCredit()) {
            evt.getTextChannel().sendMessage("Insufficient credits.").queue();
            return;
        }

        invoker.addCredit(-amount);
        receiver.addCredit(amount);

        evt.getTextChannel().sendMessage("Successfully transferred " + amount + " credit to " + m.getEffectiveName() + ".").queue();

    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
