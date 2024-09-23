package me.afarrukh.hashbot.commands.audiotracks.playlist;

import static java.util.Objects.requireNonNull;

import java.util.List;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.track.Playlist;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ViewListCommand extends Command {

    private final Database database;

    public ViewListCommand(Database database) {
        super("viewlists");
        this.database = database;
        addAlias("vl");
        addAlias("playlists");
        description = "View all your playlists by name, and how many tracks are in each of them";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        List<Playlist> playlists =
                database.getAllPlaylistsForUser(evt.getMember().getId());
        var stringBuilder = new StringBuilder();

        if (playlists.size() == 0) {
            stringBuilder
                    .append("You currently have no playlists, use the ")
                    .append(database.getPrefixForGuild(evt.getGuild().getId()))
                    .append("savelist command to save the current track queue to a playlist");

        } else {
            for (Playlist p : playlists) {
                stringBuilder
                        .append("**")
                        .append(p.getName())
                        .append(": **")
                        .append(p.getSize())
                        .append(" tracks")
                        .append("\n\n");
            }
        }

        var embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(
                "Viewing playlists for " + requireNonNull(evt.getMember()).getEffectiveName());
        embedBuilder.setDescription(stringBuilder);
        embedBuilder.setColor(Constants.EMB_COL);
        embedBuilder.setThumbnail(evt.getMember().getUser().getAvatarUrl());
        evt.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
