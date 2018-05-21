package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.utils.BotUtils;
import me.afarrukh.hashbot.utils.CmdUtils;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.ArrayList;

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
            ArrayList<Role> singularRole = new ArrayList<>();
            singularRole.add(desiredRole);

            System.out.println(evt.getGuild().getMembersWithRoles(singularRole).size());

            //If it is a gamerole, then the user must be the owner to change its colour
            if(BotUtils.isGameRole(desiredRole, evt.getGuild())) {
                if(!Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getGameRoleFromRole(desiredRole)
                        .getCreator().equalsIgnoreCase(evt.getAuthor().getId())) {
                    evt.getTextChannel().sendMessage("You need to be the owner of this role to modify it as it is a game role.").queue();
                    return;
                }

            }

            //If the role isn't a custom role (i.e. only has one member in it, then do not change it)
            if(evt.getGuild().getMembersWithRoles(singularRole).size() > 1 && !BotUtils.isGameRole(desiredRole, evt.getGuild())) {
                evt.getTextChannel().sendMessage("You cannot change this role because it is not unique to you.").queue();
                return;
            }

            if(desiredRole != null) {
                Color prevCol = desiredRole.getColor();
                String prevRed = Integer.toString(prevCol.getRed());
                String prevGreen = Integer.toString(prevCol.getGreen());
                String prevBlue = Integer.toString(prevCol.getBlue());

                if(red == 0 && green == 0 && blue == 0) {
                    evt.getTextChannel().sendMessage("You cannot use this colour. (Use at least one value above 0)").queue();
                    return;
                }

                desiredRole.getManager().setColor(new Color(red, green, blue)).queue();
                Invoker in = new Invoker(evt.getMember());

                if(!BotUtils.isGameRole(desiredRole, evt.getGuild()))
                    in.addCredit(-Constants.colChangeCred);

                evt.getTextChannel().sendMessage("Colour changed from " +prevRed+ " " + prevGreen+ " " +prevBlue
                        +" to " + red+ " " + green+ " " +blue+ " [Cost: "+Constants.colChangeCred+" credit]").queue();
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
