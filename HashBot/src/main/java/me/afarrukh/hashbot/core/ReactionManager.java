package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.GuildDataManager;
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


        Set<User> userSet = new HashSet<>();

        MessageReaction footerReaction = m.getReactions().get(0);

        for(MessageReaction reaction: m.getReactions()) {
            for(User u: reaction.getUsers()) {
                if(reaction.getCount() > footerReaction.getCount())
                    footerReaction = reaction;
                userSet.add(u);
            }
        }

        if(userSet.size() < Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPinThreshold())
            return;

        String pinnedChannelId = evt.getGuild().getTextChannelById(new GuildDataManager(evt.getGuild()).getPinnedChannelId()).getId();

        if(pinnedChannelId.equals(evt.getChannel().getId()))
            return;

        MessageChannel channel = evt.getGuild().getTextChannelById(pinnedChannelId);
        if(channel == null)
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

        channel.sendMessage(eb.build()).queue();


    }
}
