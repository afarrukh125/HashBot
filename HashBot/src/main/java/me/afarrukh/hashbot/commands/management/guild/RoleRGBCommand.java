package me.afarrukh.hashbot.commands.management.guild;

import me.afarrukh.hashbot.commands.Command;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RoleRGBCommand extends Command {

    public RoleRGBCommand() {
        super("rolergb");
        description = "Gets the red green and blue values for a particular role";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(params == null) {
            onIncorrectParams(evt.getTextChannel());
            return;
        }
        for(Role r: evt.getGuild().getRoles()) {
            if(r.getName().equalsIgnoreCase(params)) {
                int red = r.getColor().getRed();
                int green = r.getColor().getGreen();
                int blue = r.getColor().getBlue();
                evt.getTextChannel().sendMessage("Role RGB for role " +r.getName()+ ": " +red+ " " +green+ " " +blue).queue();
                return;
            }
        }
        evt.getTextChannel().sendMessage("The role " +params+ " does not exist.").queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: rolergb <role name>").queue();
    }
}
