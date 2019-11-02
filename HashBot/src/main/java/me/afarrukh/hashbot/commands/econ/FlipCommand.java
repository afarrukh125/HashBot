package me.afarrukh.hashbot.commands.econ;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.EconCommand;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.entities.Invoker;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FlipCommand extends Command implements EconCommand {

    public FlipCommand() {
        super("flip");
        addAlias("f");
        description = "Flips a coin on head or tails. You can choose an amount.";
        addParameter("amount", "The amount of credit to use on this coin flip. " +
                "You can use 'all' here if you wish to use all the credits.");
        addParameter("heads or tails", "The coin result you are expecting to win on, either heads or tails" +
                ". Can be written as 'h' or 't'");

        addExampleUsage("flip all t");
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        // Checking parameter validity
        if (params == null) {
            onIncorrectParams(evt.getChannel());
            return;
        }

        if (params.split(" ").length != 2) {
            onIncorrectParams(evt.getChannel());
            return;
        }

        // Converting user input into variables
        String[] tokens = params.split(" ");
        String choice = tokens[1];
        long amount = 0;

        Invoker invoker = Invoker.of(evt.getMember());
        // Converting user input amount from String to long
        try {
            amount = Long.parseLong(tokens[0]);
        } catch (NumberFormatException e) {
            if (tokens[0].equalsIgnoreCase("all"))
                amount = invoker.getCredit();
            else {
                evt.getChannel().sendMessage("Please enter a valid amount.").queue();
                return;
            }
        }

        // Mapping user input to number rolls.
        final Map<String, Integer> userInputMap = new HashMap<>();
        userInputMap.put("heads", 1);
        userInputMap.put("h", 1);
        userInputMap.put("head", 1);
        userInputMap.put("tails", 2);
        userInputMap.put("t", 2);
        userInputMap.put("tail", 2);

        // Checking if the user choice entered is valid
        if (userInputMap.get(tokens[1]) == null) {
            evt.getChannel().sendMessage("You must flip on either heads or tails.").queue();
            return;
        }

        if (invoker.getCredit() < amount || invoker.getCredit() <= 0) {
            evt.getChannel().sendMessage("You do not have enough credits.").queue();
            return;
        }

        // Mapping roll outcome to name
        final Map<Integer, String> outcomeMap = new HashMap<>();
        outcomeMap.put(1, "heads");
        outcomeMap.put(2, "tails");

        int rolledValue = new Random().nextInt(2) + 1;

        StringBuilder sb = new StringBuilder();
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("You flipped " + outcomeMap.get(rolledValue) + "!");

        String thumbnailPath = rolledValue == 1 ? Constants.FLIP_HEAD : Constants.FLIP_TAIL;
        eb.setThumbnail(thumbnailPath);

        if (rolledValue == userInputMap.get(choice)) {
            // If they flipped on the right choice then reward them
            sb.append("You won ").append(amount).append(" credits!");
            eb.setColor(Color.GREEN);
            invoker.addCredit(amount);
        } else {
            // Otherwise take away credits
            sb.append("You lost ").append(amount).append(" credits!");
            eb.setColor(Color.RED);
            if (invoker.getCredit() - amount < 0)
                invoker.addCredit(-invoker.getCredit());
            else
                invoker.addCredit(-amount);
        }

        sb.append("\n\n You now have ").append(invoker.getCredit()).append(" credits.");

        eb.setDescription(sb.toString());

        evt.getChannel().sendMessage(eb.build()).queue();

    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: flip <amount> <heads or tails>").queue();

    }
}
