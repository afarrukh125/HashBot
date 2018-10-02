package me.afarrukh.hashbot.commands.extras.fortnite;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.ExtrasCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.UserUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SetFortniteChannelCommand extends Command implements ExtrasCommand {

    public SetFortniteChannelCommand() {
        super("setfortnitechannel");
        addAlias("setftnchannel");
        addAlias("setfort");

        description = "Admin only. Sets the fortnite stats channel for this server.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(!evt.getMember().getPermissions().contains(Permission.ADMINISTRATOR) && !UserUtils.isBotAdmin(evt.getAuthor())) {
            evt.getTextChannel().sendMessage("Need an administrator to set this up.").queue();
            return;
        }

        Bot.extrasManager.getGuildExtrasManager(evt.getGuild()).getFortniteExtra().setFortniteChannel(evt.getTextChannel());

        evt.getMessage().delete().queue();

//        evt.getTextChannel().sendMessage(new EmbedBuilder()
//                .setColor(Constants.EMB_COL)
//                .appendDescription("Set fortnite channel to " +evt.getTextChannel().getName()).build()).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
