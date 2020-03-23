package me.afarrukh.hashbot.commands.management.bot.owner;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.OwnerCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SetNameCommand extends Command implements OwnerCommand {

    public SetNameCommand() {
        super("setname");
        description = "Sets the global discord username for the JDA instance";
        addParameter("name", "The global name to set for the bot, or more technically, the JDA instance");
        addExampleUsage("setname HashB0t");
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        evt.getJDA().getSelfUser().getManager().setName(params).queue();
    }
}
