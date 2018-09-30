package me.afarrukh.hashbot.commands.extras.fortnite;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.FortniteCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.extras.fortnite.FortniteExtra;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class FortniteRegisterCommand extends Command implements FortniteCommand {

    public FortniteRegisterCommand() {
        super("fortniteregister");
        addAlias("ftnreg");
        description = "Add your fortnite user to the current list of users for this server.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        if(!isInputValid(evt.getTextChannel(), params))
            return;
        FortniteExtra ftnExtra = Bot.extrasManager.getGuildExtrasManager(evt.getGuild()).getFortniteExtra();

        ftnExtra.addUser(evt, params);
        if(ftnExtra.getFortniteChannel() == null) {
            evt.getTextChannel().sendMessage("The user was added but the channel needs to be set. Get an administrator to set " +
                    "the fortnite channel using " + Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPrefix() + "setftnchannel" +
                    " in the appropriate channel").queue();
        }

    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Something went wrong. Use " + Bot.gameRoleManager.getGuildRoleManager(channel.getGuild()).getPrefix()
                            + "ftnreg <ps4/pc> <name> to register your user to this server").queue();
    }

    private boolean isInputValid(TextChannel channel, String params) {
        boolean valid = true;
        if(params == null)
            valid = false;
        else if(params.split(" ").length != 2)
            valid = false;
        else if(!params.split(" ")[0].equalsIgnoreCase("ps4") && !params.split(" ")[0].equalsIgnoreCase("pc")) {
            valid = false;
        }
        if(!valid)
            onIncorrectParams(channel);
        return valid;
    }
}
