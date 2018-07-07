package me.afarrukh.hashbot.commands.econ;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.entities.Invoker;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Random;

public class FlipCommand extends Command {

    public FlipCommand() {
        super("flip", new String[]{"f"});
        description = "Flips a coin on head or tails. You can choose an amount.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(params == null) {
            onIncorrectParams(evt.getTextChannel());
            return;
        }

        Invoker invoker = new Invoker(evt.getMember());

        String[] tokens = params.split(" ");

        if(tokens[0] == null || tokens[1] == null) {
            onIncorrectParams(evt.getTextChannel());
            return;
        }

        long amount = 0;
        String choice = tokens[1];

        try {
            amount = Integer.parseInt(tokens[0]);
        } catch(NumberFormatException e) {
            if(tokens[0].equalsIgnoreCase("all")) {
                long userCredit = invoker.getCredit();
                if(userCredit > 10000)
                    amount = 10000;
                else
                    amount = userCredit;
            }
        }

        if(amount > 10000) {
            evt.getTextChannel().sendMessage("You cannot provide more than 10000 credits").queue();
            return;
        }

        if(amount <= 0) {
            evt.getTextChannel().sendMessage("You must provide an amount.").queue();
            return;
        }

        if(invoker.getCredit() < amount) {
            evt.getTextChannel().sendMessage("You do not have enough credits.").queue();
            return;
        }

        int choiceToNumber = 0;

        if(choice.equalsIgnoreCase("head") || choice.equalsIgnoreCase("heads") || choice.equalsIgnoreCase("h"))
            choiceToNumber = 1;
        else if(choice.equalsIgnoreCase("tail") || choice.equalsIgnoreCase("tails") || choice.equalsIgnoreCase("t"))
            choiceToNumber = 2;
        else {
            evt.getTextChannel().sendMessage("You can only flip on heads or tails.").queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();

        Random random = new Random();
        int outcome = random.nextInt(2) + 1;

        StringBuilder sb = new StringBuilder();
        sb.append(" You");
        if(outcome == choiceToNumber) {
            sb.append(" won ");
            eb.setTitle("You won!");
            invoker.addCredit(Integer.parseInt(Long.toString(amount)));
            eb.setColor(Color.GREEN);
        }
        else {
            sb.append(" lost ");
            eb.setTitle("You lost!");
            invoker.addCredit(Integer.parseInt(Long.toString(-amount)));
            eb.setColor(Color.RED);
        }

        sb.append(amount).append(" credits.");
        sb.append(" Your current balance is ").append(invoker.getCredit()).append(".");
        eb.setDescription(sb.toString());
        evt.getTextChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: flip <amount> <heads or tails>").queue();
    }
}
