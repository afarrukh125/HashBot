package me.afarrukh.hashbot.commands.management.guild;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.data.Database;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RoleRGBCommand extends Command {

    private final Database database;

    public RoleRGBCommand(Database database) {
        super("rolergb");
        this.database = database;
        description = "Gets the RGB colour values for a particular role";
        addParameter("role name", "The name of the role for which you would like to find the RGB for");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (params == null) {
            onIncorrectParams(database, evt.getChannel().asTextChannel());
            return;
        }
        for (Role r : evt.getGuild().getRoles()) {
            if (r.getName().equalsIgnoreCase(params)) {
                if (r.getColor() == null) {
                    evt.getChannel()
                            .sendMessage("This role does not have a non-default colour")
                            .queue();
                    return;
                }
                int red = r.getColor().getRed();
                int green = r.getColor().getGreen();
                int blue = r.getColor().getBlue();
                evt.getChannel()
                        .sendMessage("Role RGB for role " + r.getName() + ": " + red + " " + green + " " + blue)
                        .queue();
                return;
            }
        }
        evt.getChannel().sendMessage("The role " + params + " does not exist.").queue();
    }

    @Override
    public void onIncorrectParams(Database database, TextChannel channel) {
        channel.sendMessage("Usage: rolergb <role name>").queue();
    }
}
