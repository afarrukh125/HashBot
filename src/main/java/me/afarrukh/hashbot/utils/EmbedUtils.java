package me.afarrukh.hashbot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.gameroles.*;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import me.afarrukh.hashbot.track.TrackScheduler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;


@SuppressWarnings("Duplicates")
public class EmbedUtils {

    /**
     * @param gmm  The guild track manager
     * @param evt  The message received event associated with the queue message request
     * @param page The page of the message queue to be displayed
     * @return An embed referring to the current queue of audio tracks playing. If not found it simply goes to the method for a single track embed.
     */
    public static MessageEmbed getQueueMsg(GuildAudioTrackManager gmm, MessageReceivedEvent evt, int page) {
        try {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Constants.EMB_COL);
            eb.setTitle("Queue for " + evt.getGuild().getName() + "\n");
            AudioTrack currentTrack = gmm.getPlayer().getPlayingTrack();

            BlockingQueue<AudioTrack> queue = gmm.getScheduler().getQueue();
            int maxPageNumber = queue.size() / 10 + 1; //We need to know how many tracks are displayed per page

            //If there are no tracks in the queue then it will just give an embedded message for a single track.
            if (queue.size() == 0) {
                return getSingleTrackEmbed(gmm.getPlayer().getPlayingTrack(), evt);
            }

            //This block of code is to prevent the list from displaying a blank page as the last one
            if (queue.size() % 10 == 0)
                maxPageNumber--;

            if (page > maxPageNumber) {
                return new EmbedBuilder().setDescription("Page " + page + " out of bounds.").setColor(Constants.EMB_COL).build();
            }

            Iterator<AudioTrack> iter = gmm.getScheduler().getQueue().iterator();
            int startIdx = 1 + ((page - 1) * 10); //The start track on that page eg page 2 would give 11
            int targetIdx = page * 10; //The last track on that page, eg page 2 would give 20
            int count = 1;
            eb.appendDescription("__Now Playing:__\n[" + currentTrack.getInfo().title + "](" + currentTrack.getInfo().uri
                    + ") | (`" + CmdUtils.longToMMSS(currentTrack.getPosition()) + "/" + CmdUtils.longToMMSS(currentTrack.getDuration()) + "`) `queued by: "
                    + currentTrack.getUserData().toString() + "`\n\n\n__Upcoming__\n\n");
            while (iter.hasNext()) {
                AudioTrack at = iter.next();
                if (count >= startIdx && count <= targetIdx) {
                    eb.appendDescription("`" + count + ".` [" + at.getInfo().title + "](" + at.getInfo().uri
                            + ") | (`" + CmdUtils.longToMMSS(at.getDuration()) + "`) `queued by: "
                            + at.getUserData().toString() + "`\n\n");
                }
                if (count == targetIdx)
                    break;

                count++;
            }
            eb.appendDescription("\n**" + queue.size() + " tracks queued, 1 playing** | Total duration**: `" + gmm.getScheduler().getTotalQueueTime() + "` | **");
            StringBuilder sb = new StringBuilder();
            sb.append("[Page ").append(page).append("/").append(maxPageNumber).append("] ");
            if (gmm.getScheduler().isFairPlay())
                sb.append("Fairplay mode is on. Playtop command will not work as expected. Tracks are queued fairly. ");
            if (gmm.getScheduler().isLoopingQueue())
                sb.append("The queue is looping. Tracks will be added to the back of the queue once they finish");
            eb.setFooter(sb.toString(), evt.getAuthor().getAvatarUrl());
            eb.setThumbnail(AudioTrackUtils.getThumbnailURL(currentTrack));
            return eb.build();
        } catch (NullPointerException e) {
            return getNothingPlayingEmbed();
        }
    }

    /**
     * Returns an embed with the only track currently in the queue
     *
     * @param evt The event to get the channel to send it to
     * @return A message embed with information on a single provided audio track
     */
    public static MessageEmbed getSingleTrackEmbed(AudioTrack currentTrack, MessageReceivedEvent evt) {
        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder sb = new StringBuilder(); // Building the title
        sb.append("Currently playing");
        if (Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().isLooping())
            sb.append(" [Looping]");
        eb.setTitle(sb.toString());
        eb.appendDescription("[" + currentTrack.getInfo().title + "](" + currentTrack.getInfo().uri + ")\n\n");
        eb.appendDescription("**Channel**: `" + currentTrack.getInfo().author + "`\n");
        eb.appendDescription("**Queued by**: `" + currentTrack.getUserData().toString() + "`\n");
        eb.appendDescription("**Duration**: `" + CmdUtils.longToMMSS(currentTrack.getPosition()) + "/" + CmdUtils.longToMMSS(currentTrack.getDuration()) + "`\n\n");
        String trackBar = AudioTrackUtils.getAudioTrackBar(currentTrack);
        eb.appendDescription(trackBar);
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(AudioTrackUtils.getThumbnailURL(currentTrack));
        return eb.build();
    }

    /**
     * @param gmm The guild track manager associated with the embed being requested
     * @param at  The audio track which has been queued
     * @param evt The message received event containing information such as which channel to send to
     * @return an embed referring to a track which has been queued to an audioplayer already playing a track
     */
    public static MessageEmbed getQueuedEmbed(GuildAudioTrackManager gmm, AudioTrack at, MessageReceivedEvent evt) {
        TrackScheduler ts = gmm.getScheduler();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Now playing");
        eb.setColor(Constants.EMB_COL);
        eb.appendDescription("[" + at.getInfo().title + "](" + at.getInfo().uri + ")\n\n");
        eb.appendDescription("**Channel**: `" + at.getInfo().author + "`\n");
        eb.appendDescription("**Queued by**: `" + at.getUserData().toString() + "`\n");
        eb.appendDescription("**Duration**: `" + CmdUtils.longToMMSS(at.getDuration()) + "`\n");
        if (!ts.getQueue().isEmpty() || gmm.getPlayer().getPlayingTrack() != null) {
            eb.setTitle("Queued track");
            eb.appendDescription("**Position in queue**: `" + ts.getTrackIndex(at) + "`\n");
            eb.appendDescription("**Playing in approximately**: `" + ts.getTotalTimeTil(at) + "`\n");
            if (ts.isFairPlay())
                eb.setFooter("Fairplay mode is currently on. Use " + Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPrefix() + "fairplay to turn it off.", null);
        }
        eb.setThumbnail(AudioTrackUtils.getThumbnailURL(at));

        return eb.build();
    }

    /**
     * @param at  The audio track which has been skipped to
     * @param evt The message received event associated with the skip embed request
     * @return Returns an embed referring to the track which has been skipped to
     */
    public static MessageEmbed getSkippedToEmbed(AudioTrack at, MessageReceivedEvent evt) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Skipped to");
        eb.setColor(Constants.EMB_COL);
        eb.appendDescription("[" + at.getInfo().title + "](" + at.getInfo().uri + ")\n\n");
        eb.appendDescription("**Channel**: `" + at.getInfo().author + "`\n");
        eb.appendDescription("**Queued by**: `" + at.getUserData().toString() + "`\n");
        eb.appendDescription("**Duration**: `" + CmdUtils.longToMMSS(at.getDuration()) + "`\n");
        eb.setThumbnail(AudioTrackUtils.getThumbnailURL(at));
        return eb.build();
    }

    /**
     * @param at The audio track which has been skipped
     * @return an embed referring to the track which has been skipped, not to be confused with getSkippedToEmbed
     */
    public static MessageEmbed getSkippedEmbed(AudioTrack at) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Skipped track");
        eb.appendDescription("[" + at.getInfo().title + "](" + at.getInfo().uri + ")\n\n");
        eb.setColor(Constants.EMB_COL);

        eb.setThumbnail(AudioTrackUtils.getThumbnailURL(at));

        return eb.build();
    }

    /**
     * @param gmm The track manager to query
     * @param at  The audiotrack which is being added
     * @param evt The event (contains information about the channel which queued it, the guild etc.
     * @return A message embed with the appropriate information for a track that has been queued to the top
     */
    public static MessageEmbed getQueuedTopEmbed(GuildAudioTrackManager gmm, AudioTrack at, MessageReceivedEvent evt) {
        TrackScheduler ts = gmm.getScheduler();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Queued track to top");
        eb.setColor(Constants.EMB_COL);
        eb.appendDescription("[" + at.getInfo().title + "](" + at.getInfo().uri + ")\n\n");
        eb.appendDescription("**Channel**: `" + at.getInfo().author + "`\n");
        eb.appendDescription("**Queued by**: `" + at.getUserData().toString() + "`\n");
        eb.appendDescription("**Duration**: `" + CmdUtils.longToMMSS(at.getDuration()) + "`\n");
        if (!ts.getQueue().isEmpty() || gmm.getPlayer().getPlayingTrack() != null) {
            eb.appendDescription("**Position in queue**: `1`\n");
            String totalTime = CmdUtils.longToHHMMSS(gmm.getPlayer().getPlayingTrack().getDuration() - gmm.getPlayer().getPlayingTrack().getPosition());
            eb.appendDescription("**Playing in approximately**: `" + totalTime + "`\n");
        }
        eb.setThumbnail(AudioTrackUtils.getThumbnailURL(at));

        return eb.build();
    }

    /**
     * Gets an embed that returns a playlist that has been queued
     *
     * @param gmm      The guild track manager which has an audioplayer which will have this playlist added to
     * @param playlist The playlist to be added
     * @return the MessageEmbed object to represent this playlist that has been queued
     */
    public static MessageEmbed getPlaylistEmbed(GuildAudioTrackManager gmm, AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if (firstTrack == null)
            firstTrack = playlist.getTracks().get(0);


        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Playlist added: " + playlist.getName());
        eb.appendDescription("**Queued by**: `" + firstTrack.getUserData().toString() + "`\n");
        eb.appendDescription("**Number of tracks**: `" + playlist.getTracks().size() + "`\n");
        eb.appendDescription("**Total duration**: `" + AudioTrackUtils.getPlaylistDuration(playlist) + "`\n");


        eb.setThumbnail(AudioTrackUtils.getThumbnailURL(firstTrack));
        return eb.build();
    }

    /**
     * @param memberList An array of members which are sorted by order of experience
     * @param evt        The message received event associated with the leaderboard request
     * @return An embed that refers to the leaderboard of the users sorted by their credit
     */
    public static MessageEmbed getLeaderboard(java.util.List<Member> memberList, MessageReceivedEvent evt) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setTitle("The leaderboard for " + evt.getGuild().getName() + ":");

        int VALUE = Constants.LEADERBOARD_MAX;

        if (memberList.size() < VALUE) //If the server does not have enough players to have a leaderboard of 5
            VALUE = memberList.size();

        for (int i = 0; i < VALUE; i++) {
            Invoker inv = Invoker.of(memberList.get(i));
            eb.appendDescription((i + 1) + ". **" + memberList.get(i).getUser().getName() + "** " + "| `Level: " + inv.getLevel() + "` | `Experience: "
                    + inv.getExp() + "/" + inv.getExpForNextLevel() + "`\n\n");
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

    public static MessageEmbed getRoleName(String name) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setTitle("Choose your new role name");
        eb.setDescription("Current name is: " + name);
        return eb.build();
    }

    public static MessageEmbed getRoleRed(int number) {
        EmbedBuilder eb = new EmbedBuilder();
        try {
            eb.setColor(new Color(number, 0, 0));
        } catch (IllegalArgumentException e) {
            eb.setDescription("That is an invalid colour. Please select a number between 0-255");
        }

        eb.setTitle("Choose your new role red colour");
        eb.setDescription("The colour to the left is the current intensity of red: " + number);
        return eb.build();
    }

    public static MessageEmbed getRoleGreen(int number) {
        EmbedBuilder eb = new EmbedBuilder();
        try {
            eb.setColor(new Color(0, number, 0));
        } catch (IllegalArgumentException e) {
            eb.setDescription("That is an invalid colour. Please select a number between 0-255");
        }

        eb.setTitle("Choose your new role green colour");
        eb.setDescription("The colour to the left is the current intensity of green: " + number);
        return eb.build();
    }

    public static MessageEmbed getRoleBlue(int number) {
        EmbedBuilder eb = new EmbedBuilder();
        try {
            eb.setColor(new Color(0, 0, number));
        } catch (IllegalArgumentException e) {
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
        eb.setColor(rb.getColor());
        String cap = rb.roleName.substring(0, 1).toUpperCase() + rb.roleName.substring(1);
        eb.setDescription("Please confirm that this is the name and colour you wish to have for the new role.");
        eb.setTitle(cap);
        return eb.build();
    }

    public static MessageEmbed getRoleCompleteEmbed(RoleBuilder rb) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(rb.getColor());
        String cap = rb.roleName.substring(0, 1).toUpperCase() + rb.roleName.substring(1);
        eb.setDescription("The new role has been created.");
        eb.setTitle(cap);
        eb.setThumbnail(rb.getGuild().getIconUrl());
        return eb.build();
    }

    public static MessageEmbed getInvalidRoleEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setDescription("The new role could not be created because the colour or name is invalid. Please try again.");
        eb.setTitle("Error.");
        return eb.build();
    }

    public static MessageEmbed getRoleExistsEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setDescription("The new role could not be created because this role already exists or you already have it.");
        eb.setTitle("Error.");
        return eb.build();
    }

    public static <T extends RoleGUI> MessageEmbed getGameRoleListEmbed(T roleGUI, int page, java.util.List<GameRole> roleList) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(Constants.EMB_COL);
        eb.setTitle("List of game roles for " + roleGUI.getGuild().getName());

        //If there are no tracks in the queue then it will just give an embedded message for a single track.
        if (roleList.size() == 0)
            return new EmbedBuilder().setTitle("No game roles")
                    .setColor(Constants.EMB_COL)
                    .appendDescription("Use **createrole** to add roles.")
                    .build();


        int maxPageNumber = roleList.size() / 10 + 1; //We need to know how many tracks are displayed per page

        //This block of code is to prevent the list from displaying a blank page as the last one
        if (roleList.size() % 10 == 0)
            maxPageNumber--;

        if (page > maxPageNumber)
            return new EmbedBuilder().setDescription("Page " + page + " out of bounds.").setColor(Constants.EMB_COL).build();


        String[] emojiNumArr = BotUtils.createStandardNumberEmojiArray();
        Iterator<GameRole> iter = Bot.gameRoleManager.getGuildRoleManager(roleGUI.getGuild()).getGameRoles().iterator();
        int startIdx = 1 + ((page - 1) * 10); //The start track on that page eg page 2 would give 11
        int targetIdx = page * 10; //The last track on that page, eg page 2 would give 20
        int count = 1;
        while (iter.hasNext()) {
            GameRole gameRole = iter.next();
            if (count >= startIdx && count <= targetIdx)
                eb.appendDescription(emojiNumArr[(count - 1) % 10] + " " + gameRole.getName() + "\n\n");

            if (count == targetIdx)
                break;

            count++;
        }
        eb.setTitle("Roles for " + roleGUI.getGuild().getName() + " (Page " + page + "/" + maxPageNumber + ")");
        eb.setThumbnail(roleGUI.getGuild().getIconUrl());

        if (Bot.gameRoleManager.getGuildRoleManager(roleGUI.getGuild()).getGameRoles().size() > 10)
            eb.setFooter("Use the arrow reaction to move to the next page of roles", null);

        return eb.build();
    }

    public static MessageEmbed confirmDesiredRole(RoleAdder ra) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(ra.getGuild().getIconUrl());
        eb.setTitle("Confirm role");
        eb.appendDescription("Please confirm that you would like the following role: " + ra.getDesiredRole().getName());
        return eb.build();
    }

    public static MessageEmbed confirmDeleteRole(RoleDeleter rd) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);

        eb.setThumbnail(rd.getUser().getAvatarUrl());
        eb.setTitle("Confirm role deletion");
        eb.appendDescription("Please confirm that you would like to delete the following role " + rd.getRoleToBeDeleted().getName());
        return eb.build();
    }

    public static <T extends RoleGUI> MessageEmbed deleteRoleCompleteEmbed(T roleGUI) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(roleGUI.getUser().getAvatarUrl());
        eb.setTitle("Role deletion complete.");
        eb.setDescription("The selected role was deleted from this guilds list of game roles.");
        return eb.build();
    }

    public static MessageEmbed getNullRoleEmbed(Guild guild) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(guild.getIconUrl());
        eb.setTitle("Error");
        eb.appendDescription("The role you have selected is invalid. Please try again. (It may not exist)");
        return eb.build();
    }

    public static MessageEmbed addRoleCompleteEmbed(Role r) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(r.getGuild().getIconUrl());
        eb.setTitle("Role added");
        eb.appendDescription("You now have the role " + r.getName() + ". Remember you can use " +
                Bot.gameRoleManager.getGuildRoleManager(r.getGuild()).getPrefix() + "removerole to remove this role.");
        return eb.build();
    }

    public static MessageEmbed alreadyHasRoleEmbed(Role r) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(r.getGuild().getIconUrl());
        eb.setTitle("Error");
        eb.appendDescription("You already have the role " + r.getName());
        return eb.build();
    }

    public static <T extends RoleGUI> MessageEmbed getCreatedRolesEmbed(T roleGUI, int page, java.util.List<GameRole> createdRoles) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);

        if (createdRoles.size() == 0) {
            Bot.gameRoleManager.getGuildRoleManager(roleGUI.getGuild()).getRoleModifiers().remove(roleGUI);
            return new EmbedBuilder().setTitle("No game roles")
                    .setColor(Constants.EMB_COL)
                    .appendDescription("There are no GameRoles created by you. Use **createrole** to do this.")
                    .build();
        }

        int maxPageNumber = createdRoles.size() / 10 + 1;

        if (createdRoles.size() % 10 == 0)
            maxPageNumber--;

        if (page > maxPageNumber)
            return new EmbedBuilder().setDescription("Page " + page + " out of bounds.").setColor(Constants.EMB_COL).build();

        String[] emojiNumArr = BotUtils.createStandardNumberEmojiArray();
        Iterator<GameRole> iter = createdRoles.iterator();
        int startIdx = 1 + ((page - 1) * 10); //The start track on that page eg page 2 would give 11
        int targetIdx = page * 10; //The last track on that page, eg page 2 would give 20
        int count = 1;
        while (iter.hasNext()) {
            GameRole gameRole = iter.next();
            if (count >= startIdx && count <= targetIdx)
                eb.appendDescription(emojiNumArr[(count - 1) % 10] + " " + gameRole.getName() + "\n\n");

            if (count == targetIdx)
                break;

            count++;
        }

        eb.setTitle("Roles created by " + roleGUI.getUser().getName() + " (Page " + page + "/" + maxPageNumber + ")");
        eb.setThumbnail(roleGUI.getUser().getAvatarUrl());

        return eb.build();
    }

    public static <T extends RoleGUI> MessageEmbed getUserGameRoleListEmbed(T roleGUI, int page) {
        EmbedBuilder eb = new EmbedBuilder();
        java.util.List<GameRole> roleList = Invoker.of(roleGUI.getGuild().getMember(roleGUI.getUser())).getGameRoles();

        eb.setColor(Constants.EMB_COL);
        eb.setTitle("List of game roles for " + roleGUI.getUser().getName());

        //If there are no roles in the server...
        if (roleList.size() == 0) {
            return new EmbedBuilder().setTitle("No game roles")
                    .setColor(Constants.EMB_COL)
                    .appendDescription("Use **addrole** to add roles.")
                    .build();
        }

        int maxPageNumber = roleList.size() / 10 + 1; //We need to know how many tracks are displayed per page

        //This block of code is to prevent the list from displaying a blank page as the last one
        if (roleList.size() % 10 == 0)
            maxPageNumber--;

        if (page > maxPageNumber)
            return new EmbedBuilder().setDescription("Page " + page + " out of bounds.").setColor(Constants.EMB_COL).build();


        String[] emojiNumArr = BotUtils.createStandardNumberEmojiArray();
        Iterator<GameRole> iter = roleList.iterator();
        int startIdx = 1 + ((page - 1) * 10); //The start track on that page eg page 2 would give 11
        int targetIdx = page * 10; //The last track on that page, eg page 2 would give 20
        int count = 1;
        while (iter.hasNext()) {
            GameRole gameRole = iter.next();
            if (count >= startIdx && count <= targetIdx)
                eb.appendDescription(emojiNumArr[(count - 1) % 10] + " " + gameRole.getName() + "\n\n");
            if (count == targetIdx)
                break;

            count++;
        }
        eb.setTitle("Roles for " + roleGUI.getUser().getName() + " (Page " + page + "/" + maxPageNumber + ")");
        eb.setThumbnail(roleGUI.getUser().getAvatarUrl());
        return eb.build();
    }

    public static MessageEmbed confirmRemoveRole(RoleGUI roleGUI, GameRole gameRole) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(roleGUI.getUser().getAvatarUrl());
        eb.setTitle("Confirm role removal");
        eb.appendDescription("Please confirm that you would like to remove the following role: " + gameRole.getName());

        return eb.build();
    }

    public static <T extends RoleGUI> MessageEmbed getNullRoleEmbed(T roleGUI) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(roleGUI.getUser().getAvatarUrl());
        eb.setTitle("Error");
        eb.appendDescription("The role you have selected is invalid. Please try again. (It may not exist)");
        return eb.build();
    }

    public static <T extends RoleGUI> MessageEmbed alreadyHasRoleEmbed(T roleGUI) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(roleGUI.getUser().getAvatarUrl());
        eb.setTitle("Error");
        eb.appendDescription("You already have this role.");
        return eb.build();
    }

    public static <T extends RoleGUI> MessageEmbed addRoleRemovedEmbed(T roleGUI, GameRole gr) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setThumbnail(roleGUI.getUser().getAvatarUrl());
        eb.setTitle("Role removed");
        eb.appendDescription("You no longer have the role " + gr.getName());
        return eb.build();
    }

    public static MessageEmbed getCreditsLeaderboardEmbed(java.util.List<Member> memberList, MessageReceivedEvent evt) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);
        eb.setTitle("Credits leaderboard for " + evt.getGuild().getName());

        int maxIndex = memberList.size();

        if (maxIndex > 10)
            maxIndex = 10;

        for (int i = 0; i < maxIndex; i++) {
            Invoker invoker = Invoker.of(memberList.get(i));
            eb.appendDescription((i + 1) + ". | **" + invoker.getMember().getEffectiveName() + "** | `Credits: " + invoker.getCredit() + "`\n\n");
        }

        eb.setThumbnail(evt.getGuild().getIconUrl());
        return eb.build();
    }
}
