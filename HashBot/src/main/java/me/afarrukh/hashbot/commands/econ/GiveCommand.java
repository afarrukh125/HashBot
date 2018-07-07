package me.afarrukh.hashbot.commands.econ;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.entities.Invoker;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class GiveCommand extends Command {

    public GiveCommand() {
        super("give", new String[]{"transfer"});
        description = "Transfers credit to the mentioned user";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(params == null) {
            onIncorrectParams(evt.getTextChannel());
            return;
        }
        if(evt.getMessage().getMentionedMembers().isEmpty()) {
            evt.getTextChannel().sendMessage("You must mention a member as second parameter.").queue();
            return;
        }

        Member m = evt.getMessage().getMentionedMembers().get(0);
        int amount = 0;
        String[] tokens = params.split(" ");

        try {
            amount = Integer.parseInt(tokens[tokens.length-1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | NullPointerException e) {
            evt.getTextChannel().sendMessage("You must provide a numerical amount to give to the mentioned user.").queue();
            return;
        }

        if(amount <= 0) {
            evt.getTextChannel().sendMessage("You must provide an amount to transfer.").queue();
            return;
        }

        Invoker invoker = new Invoker(evt.getMember());
        Invoker receiver = new Invoker(m);

        if(amount > invoker.getCredit()) {
            evt.getTextChannel().sendMessage("Insufficient credits.").queue();
            return;
        }

        invoker.addCredit(-amount);
        receiver.addCredit(amount);

        evt.getTextChannel().sendMessage("Successfully transferred " +amount+ " credit to " +m.getEffectiveName() + ".").queue();

    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
