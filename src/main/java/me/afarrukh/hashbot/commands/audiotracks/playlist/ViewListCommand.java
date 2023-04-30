package me.afarrukh.hashbot.commands.audiotracks.playlist;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.track.Playlist;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class ViewListCommand extends Command {

    public ViewListCommand() {
        super("viewlists");
        addAlias("vl");
        addAlias("playlists");
        description = "View all your playlists by name, and how many tracks are in each of them";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        List<Playlist> playlists = new SQLUserDataManager(evt.getMember()).viewAllPlaylists();

        StringBuilder stringBuilder = new StringBuilder();

        if (playlists.size() == 0) {
            stringBuilder
                    .append("You currently have no playlists, use the ")
                    .append(Bot.prefixManager
                            .getGuildRoleManager(evt.getGuild())
                            .getPrefix())
                    .append("savelist command to save the current track queue to a playlist");

        } else {
            for (Playlist p : playlists)
                stringBuilder
                        .append("**")
                        .append(p.getName())
                        .append(": **")
                        .append(p.getSize())
                        .append(" tracks")
                        .append("\n\n");
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Viewing playlists for "
                + Objects.requireNonNull(evt.getMember()).getEffectiveName());
        eb.setDescription(stringBuilder);
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(evt.getMember().getUser().getAvatarUrl());
        evt.getChannel().sendMessageEmbeds(eb.build()).queue();
    }
}
