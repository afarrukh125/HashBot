package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.utils.LevelUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class StatsCommand extends Command {

    public StatsCommand() {
        super("stats");
        description = "Allows you to view your credit, level and experience";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        Invoker inv = new Invoker(evt.getMember());

        int exp = (int) inv.getExp();
        int level = (int) inv.getLevel();
        int expToProgress = inv.getExpForNextLevel();

        int remainder = (int) Math.floor((double) exp/expToProgress*10);

        String expBar = LevelUtils.getBar(remainder);
        evt.getTextChannel().sendMessage("Your current credit is " +inv.getCredit()
                + "\nYour current level is " + level
                + "\nYour current experience is " +exp+"/"+expToProgress+ "\nProgress to next level: ["
                + expBar+"]").queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
