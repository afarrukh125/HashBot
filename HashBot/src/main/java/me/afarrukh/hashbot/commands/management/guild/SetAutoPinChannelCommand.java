package me.afarrukh.hashbot.commands.management.guild;

import me.afarrukh.hashbot.commands.AdminCommand;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

/**
 * Created by Abdullah on 15/04/2019 16:12
 */
public class SetAutoPinChannelCommand extends Command implements AdminCommand {

    public SetAutoPinChannelCommand() {
        super("setautopin");
        addAlias("sp");
        addAlias("sap");

        description = "Sets whether a given channel should automatically pin messages to the pinned channel instead of the normal discord pinning method";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(params == null) {
            evt.getTextChannel().sendMessage("You need to provide the channel name or ID that you wish to add to the list of autopin channels.").queue();
            return;
        }

        TextChannel tc = evt.getGuild().getTextChannelById(params);
        if(tc == null) {
            List<TextChannel> channelList = evt.getGuild().getTextChannelsByName(params, true);

            if(channelList.isEmpty()) {
                evt.getTextChannel().sendMessage("There is no channel with this name.").queue();
                return;
            }
            if(channelList.size() > 1) {
                evt.getTextChannel().sendMessage("There is more than one channel with this name. Either provide the ID of this channel through developer mode " +
                        "or change channel names. For more on enabling and using developer mode visit https://discordia.me/developer-mode then right click the channel and click copy ID")
                        .queue();
                return;
            }
            tc = channelList.get(0);
        }

        if(tc == null) {
            evt.getTextChannel().sendMessage("There is no channel with this name.").queue();
            return;
        }
        String channelId = tc.getId();

        GuildDataManager gdm = GuildDataMapper.getInstance().getDataManager(evt.getGuild());
        if(gdm.getAutoPinChannels().contains(channelId)) {
            evt.getTextChannel().sendMessage("The provided channel is already an autopin channel.").queue();
            return;
        }

        gdm.addAutoPinChannel(channelId);

        evt.getTextChannel().sendMessage("The channel " + tc.getName() + " will no longer allow normal pins and will automatically add to the pinned channel instead.")
                .queue();

    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
