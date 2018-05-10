package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.utils.CmdUtils;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.Color;

public class ColourChangeCommand extends Command {

    public ColourChangeCommand() {
        super("rolecol");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        Invoker invoker = new Invoker(evt.getMember());
        if(invoker.getCredit() < Constants.colChangeCred) {
            evt.getTextChannel().sendMessage("You need at least " +Constants.colChangeCred+ " credit to change your colour.").queue();
            return;
        }
        try {
            String[] tokens = params.split(" ");
            if(tokens.length < 4) {
                onIncorrectParams(evt.getTextChannel());
                return;
            }
            int maxIndex = tokens.length - 1;
            String roleName = CmdUtils.getParamsAsString(tokens, 0, maxIndex - 3);
            int red = Integer.parseInt(tokens[maxIndex-2]);
            int green = Integer.parseInt(tokens[maxIndex-1]);
            int blue = Integer.parseInt(tokens[maxIndex]);
            Role desiredRole = invoker.getRole(roleName);

            if(desiredRole != null) {
                Color prevCol = desiredRole.getColor();
                String prevRed = Integer.toString(prevCol.getRed());
                String prevGreen = Integer.toString(prevCol.getGreen());
                String prevBlue = Integer.toString(prevCol.getBlue());

                desiredRole.getManager().setColor(new Color(red, green, blue)).queue();
                Invoker in = new Invoker(evt.getMember());
                in.addCredit(-Constants.colChangeCred);
                evt.getTextChannel().sendMessage("Colour changed from " +prevRed+ " " + prevGreen+ " " +prevBlue
                        +" to " + red+ " " + green+ " " +blue+ " [Cost: +"+Constants.colChangeCred+" credit]").queue();
                return;
            }
            evt.getTextChannel().sendMessage("You do not have the role, or it doesn't exist.").queue();


        } catch(NumberFormatException | NullPointerException e) {
            onIncorrectParams(evt.getTextChannel());
        } catch(IllegalArgumentException e) {
            evt.getTextChannel().sendMessage("The values of colours must be in the range 0-255.").queue();
        }

    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Correct usage: rolecol <role name> <red> <green> <blue>").queue();
    }
}
