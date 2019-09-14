package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.utils.BotUtils;
import me.afarrukh.hashbot.utils.CmdUtils;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;

public class ColourChangeCommand extends Command {

    public ColourChangeCommand() {
        super("rolecol");
        description = "Allows you to change the colour for one of your roles";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        Invoker invoker = new Invoker(evt.getMember());
        if (invoker.getCredit() < Constants.colChangeCred) {
            evt.getTextChannel().sendMessage("You need at least " + Constants.colChangeCred + " credit to change your colour.").queue();
            return;
        }
        try {
            String roleName;
            String[] tokens = params.split(" ");
            int maxIndex = tokens.length - 1;
            int red;
            int green;
            int blue;

            if (tokens[maxIndex].startsWith("#")) {
                roleName = CmdUtils.getParamsAsString(tokens, 0, maxIndex - 1);

                Color colorFromHex = Color.decode(tokens[maxIndex]);
                red = colorFromHex.getRed();
                green = colorFromHex.getGreen();
                blue = colorFromHex.getBlue();
            } else {
                if (tokens.length < 4) {
                    onIncorrectParams(evt.getTextChannel());
                    return;
                }
                roleName = CmdUtils.getParamsAsString(tokens, 0, maxIndex - 3);

                red = Integer.parseInt(tokens[maxIndex - 2]);
                green = Integer.parseInt(tokens[maxIndex - 1]);
                blue = Integer.parseInt(tokens[maxIndex]);
            }

            Role desiredRole = invoker.getRole(roleName);

            if (desiredRole == null) {
                evt.getTextChannel().sendMessage("You do not have this role so you cannot modify it.").queue();
                BotUtils.deleteLastMsg(evt);
                return;
            }

            ArrayList<Role> singularRole = new ArrayList<>();
            singularRole.add(desiredRole);

            //If the role isn't a custom role (i.e. only has one member in it, then do not change it)
            if (evt.getGuild().getMembersWithRoles(singularRole).size() > 1 && !BotUtils.isGameRole(desiredRole, evt.getGuild())) {
                evt.getTextChannel().sendMessage("You cannot change this role because it is not unique to you.").queue();
                return;
            }

            Color prevCol = desiredRole.getColor();
            String prevRed = Integer.toString(prevCol.getRed());
            String prevGreen = Integer.toString(prevCol.getGreen());
            String prevBlue = Integer.toString(prevCol.getBlue());

            if (red == 0 && green == 0 && blue == 0) {
                evt.getTextChannel().sendMessage("You cannot use this colour. (Use at least one value above 0)").queue();
                return;
            }

            desiredRole.getManager().setColor(new Color(red, green, blue)).queue();
            Invoker in = new Invoker(evt.getMember());
            in.addCredit(-Constants.colChangeCred);

            evt.getTextChannel().sendMessage("Colour changed from " + prevRed + " " + prevGreen + " " + prevBlue
                    + " to " + red + " " + green + " " + blue + " [Cost: " + Constants.colChangeCred + " credit]").queue();


        } catch (NumberFormatException | NullPointerException e) {
            onIncorrectParams(evt.getTextChannel());
        } catch (IllegalArgumentException ignore) {
        }

    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Correct usage: rolecol <role name> <red> <green> <blue> OR rolecol <role name> <#hex>").queue();
    }
}
