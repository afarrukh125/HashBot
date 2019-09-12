package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.data.SQLUserDataManager;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Abdullah
 * Created on 10/09/2019 at 22:59
 */
public class LoadListCommand extends Command implements MusicCommand {

    public LoadListCommand() {
        super("loadlist");
        addAlias("plist");
        addAlias("dlist");

        description = "Load a playlist by name from your selection";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        new SQLUserDataManager(evt.getMember()).getPlaylistByName(params);
        //MusicUtils.connectToChannel(evt.getMember());
        evt.getTextChannel().sendMessage("Queued playlist " + params).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
