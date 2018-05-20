package me.afarrukh.hashbot.utils;

import java.awt.*;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.gameroles.RoleBuilder;
import me.afarrukh.hashbot.music.GuildMusicManager;
import me.afarrukh.hashbot.music.TrackScheduler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;


public class EmbedUtils {

    /**
     *
     * @param gmm
     * @param evt
     * @param page
     * @return An embed referring to the current queue of audio tracks playing. If not found it simply goes to the method for a single song embed.
     */
    public static MessageEmbed getQueueMsg(GuildMusicManager gmm, MessageReceivedEvent evt, int page) {
        try {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Constants.EMB_COL);
            eb.setTitle("Queue for "+evt.getGuild().getName()+"\n");
            AudioTrack currentTrack = gmm.getPlayer().getPlayingTrack();

            BlockingQueue<AudioTrack> queue = gmm.getScheduler().getQueue();
            int maxPageNumber = queue.size()/10+1; //We need to know how many songs are displayed per page

            //If there are no songs in the queue then it will just give an embedded message for a single song.
            if(queue.size() == 0) {
                return getSingleSongEmbed(gmm.getPlayer().getPlayingTrack(), evt);
            }

            //This block of code is to prevent the list from displaying a blank page as the last one
            if(queue.size()%10 == 0)
                maxPageNumber--;

            if(page > maxPageNumber) {
                return new EmbedBuilder().setDescription("Page "+page+ " out of bounds.").setColor(Constants.EMB_COL).build();
            }

            Iterator<AudioTrack> iter = gmm.getScheduler().getQueue().iterator();
            int startIdx = 1 + ((page-1)*10); //The start song on that page eg page 2 would give 11
            int targetIdx = page * 10; //The last song on that page, eg page 2 would give 20
            int count = 1;
            eb.appendDescription("__Now Playing:__\n[" +currentTrack.getInfo().title+ "](" +currentTrack.getInfo().uri
                    + ") | (`"+CmdUtils.longToMMSS(currentTrack.getDuration())+"`) `queued by: "
                    +currentTrack.getUserData().toString()+ "`\n\n\n__Upcoming__\n\n");
            while(iter.hasNext()) {
                AudioTrack at = iter.next();
                if(count >= startIdx && count<=targetIdx) {
                    eb.appendDescription("`"+count+ ".` ["+at.getInfo().title+ "](" +at.getInfo().uri
                            + ") | (`"+CmdUtils.longToMMSS(at.getDuration())+"`) `queued by: "
                            +at.getUserData().toString()+ "`\n\n");
                }
                if(count==targetIdx)
                    break;

                count++;
            }
            eb.appendDescription("\n**"+queue.size()+ " songs queued** | Total duration**: `"+gmm.getScheduler().getTotalQueueTime()+ "` | **");
            eb.setFooter("[Page "+page+"/"+maxPageNumber+"]", evt.getAuthor().getAvatarUrl());
            eb.setThumbnail(MusicUtils.getThumbnailURL(currentTrack));
            return eb.build();
        } catch(NullPointerException e) {
            return getNothingPlayingEmbed();
        }
    }

    /**
     * Returns an embed with the only song currently in the queue
     * @param evt The event to get the channel to send it to
     * @return
     */
    public static MessageEmbed getSingleSongEmbed(AudioTrack currentTrack, MessageReceivedEvent evt) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Currently playing");
        eb.appendDescription("["+currentTrack.getInfo().title+"]("+currentTrack.getInfo().uri+")\n\n");
        eb.appendDescription("**Channel**: `" +currentTrack.getInfo().author + "`\n");
        eb.appendDescription("**Queued by**: `"+currentTrack.getUserData().toString()+ "`\n");
        eb.appendDescription("**Duration**: `"+CmdUtils.longToMMSS(currentTrack.getPosition())+"/"+CmdUtils.longToMMSS(currentTrack.getDuration())+ "`\n\n");
        String musicBar = MusicUtils.getMusicBar(currentTrack);
        eb.appendDescription(musicBar);
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(MusicUtils.getThumbnailURL(currentTrack));
        return eb.build();
    }

    /**
     *
     * @param gmm
     * @param at
     * @param evt
     * @return an embed referring to a song which has been queued to an audioplayer already playing a song
     */
    public static MessageEmbed getQueuedEmbed(GuildMusicManager gmm, AudioTrack at, MessageReceivedEvent evt) {
        TrackScheduler ts = gmm.getScheduler();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Now playing");
        eb.setColor(Constants.EMB_COL);
        eb.appendDescription("["+at.getInfo().title+"]("+at.getInfo().uri+")\n\n");
        eb.appendDescription("**Channel**: `" +at.getInfo().author + "`\n");
        eb.appendDescription("**Queued by**: `"+at.getUserData().toString()+ "`\n");
        eb.appendDescription("**Duration**: `"+CmdUtils.longToMMSS(at.getDuration())+ "`\n");
        if(!ts.getQueue().isEmpty() || gmm.getPlayer().getPlayingTrack()!=null) {
            eb.setTitle("Queued song");
            eb.appendDescription("**Position in queue**: `" +ts.getSongIndex(at)+"`\n");
            eb.appendDescription("**Playing in approximately**: `" +ts.getTotalTimeTil(at)+"`\n");
        }
        eb.setThumbnail(MusicUtils.getThumbnailURL(at));

        return eb.build();
    }

    /**
     *
     * @param at
     * @param evt
     * @return Returns an embed referring to the song which has been skipped to
     */
    public static MessageEmbed getSkippedToEmbed(AudioTrack at, MessageReceivedEvent evt) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Skipped to");
        eb.setColor(Constants.EMB_COL);
        eb.appendDescription("["+at.getInfo().title+"]("+at.getInfo().uri+")\n\n");
        eb.appendDescription("**Channel**: `" +at.getInfo().author + "`\n");
        eb.appendDescription("**Queued by**: `"+at.getUserData().toString()+ "`\n");
        eb.appendDescription("**Duration**: `"+CmdUtils.longToMMSS(at.getDuration())+"`\n");
        eb.setThumbnail(MusicUtils.getThumbnailURL(at));
        return eb.build();
    }

    /**
     * @param at
     * @return an embed referring to the song which has been skipped, not to be confused with getSkippedToEmbed
     */
    public static MessageEmbed getSkippedEmbed(AudioTrack at) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Skipped song");
        eb.appendDescription("["+at.getInfo().title+"]("+at.getInfo().uri+")\n\n");
        eb.setColor(Constants.EMB_COL);

        eb.setThumbnail(MusicUtils.getThumbnailURL(at));

        return eb.build();
    }

    /**
     *
     * @param gmm The music manager to query
     * @param at The audiotrack which is being added
     * @param evt The event (contains information about the channel which queued it, the guild etc.
     * @return A message embed with the appropriate information for a song that has been queued to the top
     */
    public static MessageEmbed getQueuedTopEmbed(GuildMusicManager gmm, AudioTrack at, MessageReceivedEvent evt) {
        TrackScheduler ts = gmm.getScheduler();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Queued song");
        eb.setColor(Constants.EMB_COL);
        eb.appendDescription("["+at.getInfo().title+"]("+at.getInfo().uri+")\n\n");
        eb.appendDescription("**Channel**: `" +at.getInfo().author + "`\n");
        eb.appendDescription("**Queued by**: `"+at.getUserData().toString()+ "`\n");
        eb.appendDescription("**Duration**: `"+CmdUtils.longToMMSS(at.getDuration())+ "`\n");
        if(!ts.getQueue().isEmpty() || gmm.getPlayer().getPlayingTrack()!=null) {
            eb.appendDescription("**Position in queue**: `1`\n");
            String totalTime = CmdUtils.longToHHMMSS(gmm.getPlayer().getPlayingTrack().getDuration() - gmm.getPlayer().getPlayingTrack().getPosition());
            eb.appendDescription("**Playing in approximately**: `" +totalTime+"`\n");
        }
        eb.setThumbnail(MusicUtils.getThumbnailURL(at));

        return eb.build();
    }

    /**
     * Gets an embed that returns a playlist that has been queued
     * @param gmm
     * @param playlist
     * @return the MessageEmbed object to represent this playlist that has been queued
     */
    public static MessageEmbed getPlaylistEmbed(GuildMusicManager gmm, AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if(firstTrack == null) {
            firstTrack = playlist.getTracks().get(0);
        }

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Playlist added: "+playlist.getName());
        eb.appendDescription("**Queued by**: `"+firstTrack.getUserData().toString()+ "`\n");
        eb.appendDescription("**Number of songs**: `"+playlist.getTracks().size()+"`\n");
        eb.appendDescription("**Total duration**: `"+MusicUtils.getPlaylistDuration(playlist)+ "`\n");


        eb.setThumbnail(MusicUtils.getThumbnailURL(firstTrack));
        return eb.build();
    }

    /**
     *
     * @param memberList
     * @param evt
     * @return An embed that refers to the leaderboard of the users sorted by their credit
     */
    public static MessageEmbed getLeaderboard(Member[] memberList, MessageReceivedEvent evt) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setTitle("The leaderboard for " +evt.getGuild().getName() +":");

        int VALUE = Constants.LEADERBOARD_MAX;

        if(memberList.length < VALUE) //If the server does not have enough players to have a leaderboard of 5
            VALUE = memberList.length;

        for(int i = 0; i<VALUE; i++) {
            Invoker inv = new Invoker(memberList[i]);
            eb.appendDescription((i+1)+ ". **" +memberList[i].getUser().getName()+ "** " + "| `Level: " + inv.getLevel() + "` | `Experience: "
                    +inv.getExp()+"/"+inv.getExpForNextLevel() +"`\n\n");
        }
        eb.setThumbnail(evt.getGuild().getIconUrl());
        return eb.build();
    }

    /**
     * @return An embed which informs that nothing is playing right now
     */
    public static MessageEmbed getNothingPlayingEmbed() {
        return new EmbedBuilder().setDescription("Nothing playing right now.").setColor(Constants.EMB_COL).build();
    }

    /**
     * Unused method due to the nature of it's looks. May be worked on in future. Deprecated for now
     * @param query The string that is being searched for
     * @return A message embed containing the relevant information
     * @deprecated
     */
    public static MessageEmbed getSearchingEmbed(String query) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.appendDescription(":mag:**Searching** "+query);
        eb.setTitle("Searching for query");
        eb.setThumbnail("https://afarrukh.me/thumbnails/YTLogo.png");
        return eb.build();
    }

    /**
     * @return An embed with all the commands of the bot
     */
    public static MessageEmbed getHelpMsg() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setTitle("__Commands for LiveLifeBot__ (Prefix is `" +Constants.invokerChar+"`)");
        eb.appendDescription("**play/p** `<yt link/yt search>` - Queues a song and joins the bot into the channel if it isn't already\n\n");
        eb.appendDescription("**queue/q** `<(optional) page number>` - Lists the current queue\n\n");
        eb.appendDescription("**skip/s/n** `<(optional) song number>` - Skips to next song or song provided\n\n");
        eb.appendDescription("**ptop/playtop** `<yt link/yt search>` - Queues song to the top of the list\n\n");
        eb.appendDescription("**loop** - Loops the current song, cancelled if skip command used\n\n");
        eb.appendDescription("**seek** `<time in seconds>` - Goes to desired time in currently playing song\n\n");
        eb.appendDescription("**pause** - Pauses the bot, type again to unpause\n\n");
        eb.appendDescription("**clear**- Clears the queue\n\n");
        eb.appendDescription("**join**- Joins the bot to the channel. Not sure why you'd use this instead of play\n\n");
        eb.appendDescription("**disconnect/d/dc**- Disconnects the bot from the channel\n\n");
        eb.appendDescription("**leaderboard**- Displays the current leaderboard for the server\n\n");
        eb.appendDescription("**stats**- Displays your stats\n\n");
        eb.appendDescription("**rolecol** `<role name> <red> <green> <blue>`- Changes the colour of the role if you have it\n\n");
        return eb.build();
    }

    public static MessageEmbed getRoleName(String name) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setTitle("Choose your new role name");
        eb.setDescription("Current name is: " +name);
        return eb.build();
    }

    public static MessageEmbed getRoleRed(int number) {
        EmbedBuilder eb = new EmbedBuilder();
        try {
            eb.setColor(new Color(number, 0, 0));
        } catch(IllegalArgumentException e) {
            eb.setDescription("That is an invalid colour. Please select a number between 0-255");
        }

        eb.setTitle("Choose your new role red colour");
        eb.setDescription("The colour to the left is the current intensity of red: "+ number);
        return eb.build();
    }

    public static MessageEmbed getRoleGreen(int number) {
        EmbedBuilder eb = new EmbedBuilder();
        try {
            eb.setColor(new Color(0, number, 0));
        } catch(IllegalArgumentException e) {
            eb.setDescription("That is an invalid colour. Please select a number between 0-255");
        }

        eb.setTitle("Choose your new role green colour");
        eb.setDescription("The colour to the left is the current intensity of green: " +number);
        return eb.build();
    }

    public static MessageEmbed getRoleBlue(int number) {
        EmbedBuilder eb = new EmbedBuilder();
        try {
            eb.setColor(new Color(0, 0, number));
        } catch(IllegalArgumentException e) {
            eb.setDescription("That is an invalid colour. Please select a number between 0-255");
        }

        eb.setTitle("Choose your new role blue colour");
        eb.setDescription("The colour to the left is the current intensity of blue: " + number);
        return eb.build();
    }

    public static MessageEmbed getIllegalColourEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setDescription("Illegal colour entered. Please enter a number between 0 and 255.");
        eb.setTitle("Illegal colour!");
        return eb.build();
    }

    public static MessageEmbed getRoleConfirmEmbed(RoleBuilder rb) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(rb.color);
        eb.setDescription("Please confirm that this is the name and colour you wish to have for the new role.");
        eb.setTitle(rb.roleName);
        return eb.build();
    }

    public static MessageEmbed getRoleCompleteEmbed(RoleBuilder rb) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(rb.color);
        eb.setDescription("The new role has been created.");
        eb.setTitle(rb.roleName);
        return eb.build();
    }
}
