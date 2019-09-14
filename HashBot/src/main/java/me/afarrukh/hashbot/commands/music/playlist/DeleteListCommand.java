package me.afarrukh.hashbot.commands.music.playlist;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.data.SQLUserDataManager;
import me.afarrukh.hashbot.exceptions.PlaylistException;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Abdullah
 * Created on 14/09/2019 at 16:27
 */
public class DeleteListCommand extends Command implements MusicCommand {

    public DeleteListCommand() {
        super("deletelist");
        addAlias("dl");
        description = "Delete one of your playlists by name";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        if (params == null) {
            evt.getTextChannel().sendMessage("You need to provide the name of the playlist you would like to delete." +
                    "Use " + Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPrefix() + new ViewListCommand().getName()
                    + " to view your playlists.").queue();
            return;
        }

        SQLUserDataManager dataManager = new SQLUserDataManager(evt.getMember());

        try {
            int listSize = dataManager.getPlaylistSize(params);
            dataManager.deletePlaylist(params);
            evt.getTextChannel().sendMessage("Successfully deleted the playlist " + params + " with " + listSize + " tracks").queue();
        } catch (PlaylistException e) {
            evt.getTextChannel().sendMessage("There is no playlist with the name " + params + ".").queue();
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
