package me.afarrukh.hashbot.commands.extras.fortnite;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.ExtrasCommand;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.extras.fortnite.FortniteExtra;
import me.afarrukh.hashbot.utils.APIUtils;
import me.afarrukh.hashbot.utils.CmdUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class FortniteRegisterCommand extends Command implements ExtrasCommand {

    public FortniteRegisterCommand() {
        super("fortniteregister");
        addAlias("ftnreg");
        addAlias("register");
        addAlias("reg");
        description = "Add your fortnite user to the current list of users for this server.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        if(!isInputValid(evt.getTextChannel(), params))
            return;
        FortniteExtra ftnExtra = Bot.extrasManager.getGuildExtrasManager(evt.getGuild()).getFortniteExtra();

        String[] tokens = params.split(" ");
        String platform = params.split(" ")[0].trim();

        String name = CmdUtils.getParamsAsString(tokens, 1, tokens.length-1);

        if(!userNotFound(name, platform)) {
            evt.getTextChannel().sendMessage("The user you have entered could not be found. Please ensure the platform and username are entered properly.").queue();
            return;
        }

        ftnExtra.addUser(evt, platform, name);
        if(ftnExtra.getFortniteChannel() == null) {
            evt.getTextChannel().sendMessage("The user was added but the channel needs to be set. Get an administrator to set " +
                    "the fortnite channel using " + Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPrefix() + "setftnchannel" +
                    " in the appropriate channel").queue();
        }
        else {
            evt.getTextChannel().sendMessage("The user was successfully added.").queue();
        }

    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Something went wrong. Use " + Bot.gameRoleManager.getGuildRoleManager(channel.getGuild()).getPrefix()
                            + "ftnreg <ps4/pc> <name> to register your user to this server.").queue();
    }

    private boolean isInputValid(TextChannel channel, String params) {
        boolean valid = true;
        if(params == null) {
            onIncorrectParams(channel);
            return false;
        }
        if(!params.split(" ")[0].trim().equalsIgnoreCase("ps4") && !params.split(" ")[0].trim().equalsIgnoreCase("pc")) {
            valid = false;
        }

        if(!valid)
            onIncorrectParams(channel);
        return valid;
    }

    private boolean userNotFound(String name, String platform) {
        String response = APIUtils.getResponseFromURL("https://api.fortnitetracker.com/v1/profile/"+platform+"/"+name, Constants.fortAPIHeader);
        if(response == null)
            return false;
        else return !response.contains("error");
    }
}
