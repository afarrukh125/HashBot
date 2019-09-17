package me.afarrukh.hashbot.commands.music.playlist;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.SQLUserDataManager;
import me.afarrukh.hashbot.music.Playlist;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

/**
 * @author Abdullah
 * Created on 11/09/2019 at 23:08
 */
public class ViewListCommand extends Command {

    public ViewListCommand() {
        super("viewlists");
        addAlias("vl");
        addAlias("playlists");
        description = "View all your playlists by name, and how many tracks are in each of them";
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        List<Playlist> playlistList = new SQLUserDataManager(evt.getMember()).viewAllPlaylists();

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("You currently have ").append(playlistList.size()).append(" playlists: \n\n");
        for (Playlist p : playlistList) {
            stringBuilder.append("**").append(p.getName()).append(": **").append(p.getSize()).append(" tracks").append("\n\n");
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Viewing playlists for " + evt.getMember().getEffectiveName());
        eb.setDescription(stringBuilder);
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(evt.getMember().getUser().getAvatarUrl());
        evt.getChannel().sendMessage(eb.build()).queue();

    }
}
