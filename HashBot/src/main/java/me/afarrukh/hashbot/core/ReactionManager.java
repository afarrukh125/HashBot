package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import me.afarrukh.hashbot.gameroles.RoleAdder;
import me.afarrukh.hashbot.gameroles.RoleBuilder;
import me.afarrukh.hashbot.gameroles.RoleDeleter;
import me.afarrukh.hashbot.gameroles.RoleRemover;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

class ReactionManager {

    void sendToBuilder(GuildMessageReactionAddEvent evt) {
        RoleBuilder rb = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).builderForUser(evt.getUser());
        if(rb == null)
            return;
        rb.handleEvent(evt);
    }

    void sendToAdder(GuildMessageReactionAddEvent evt) {
        RoleAdder ra = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).adderForUser(evt.getUser());
        if(ra == null)
            return;
        ra.handleEvent(evt);
    }

    void sendToRemover(GuildMessageReactionAddEvent evt) {
        RoleRemover rr = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).removerForUser(evt.getUser());
        if(rr == null)
            return;
        rr.handleEvent(evt);
    }

    public void sendToDeleter(GuildMessageReactionAddEvent evt) {
        RoleDeleter rd = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).deleterForUser(evt.getUser());
        if(rd == null)
            return;
        rd.handleEvent(evt);
    }

    /**
     * We decide here if we want to post the message to the pinned channel depending on the reaction count
     * We are testing for distinct users, hence we use the Set class. Algorithm is unfortunately O(n^2)
     * @param evt The event associated with the reaction being added
     */
    void processForPinning(GuildMessageReactionAddEvent evt) {
        Message m = evt.getChannel().getMessageById(evt.getMessageId()).complete();
        if(m == null) // Message might not exist since it might have been deleted
            return;

        final String reactionId = "\uD83D\uDCCC"; // Pushpin emote ID

        // Getting the pinned channel ID from storage
        GuildDataManager gdm = GuildDataMapper.getInstance().getDataManager(evt.getGuild());

        if(gdm.isPinned(m.getId()))
            return;

        String pinnedChannelId = evt.getGuild().getTextChannelById(gdm.getPinnedChannelId()).getId();

        // Checking if the current channel is the pinned channel. If it is then we od not proceed
        if(pinnedChannelId.equals(evt.getChannel().getId()))
            return;

        // Checking if the pinned channel has been set
        MessageChannel channel = evt.getGuild().getTextChannelById(pinnedChannelId);
        if(channel == null)
            return;

        int size = 0;

        for(MessageReaction reaction: m.getReactions()) {
            if(reaction.getReactionEmote().getName().equals(reactionId)) {
                size = reaction.getCount();
                break;
            }
        }

        // If no reactions have been added then return
        if(size == 0)
            return;

        // If we haven't yet passed the threshold then return
        if(size < Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPinThreshold())
            return;

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);

        eb.appendDescription(m.getContentRaw());
        eb.setTitle(m.getMember().getEffectiveName());

        // Set the embed's image to be the attachment's image ONLY if it is an image
        if(!m.getAttachments().isEmpty()) {
            if (m.getAttachments().get(0).isImage())
                eb.setImage(m.getAttachments().get(0).getUrl());
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(m.getCreationTime().toInstant().toEpochMilli());

        eb.setFooter(evt.getChannel().getName() + " - " + calendar.getTime().toString()
                ,
                m.getAuthor().getAvatarUrl());

        Message newMessage = channel.sendMessage(eb.build()).complete();

        gdm.addAsPinned(m.getId(), newMessage.getId());


    }
}
