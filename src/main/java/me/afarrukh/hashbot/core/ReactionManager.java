package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import me.afarrukh.hashbot.gameroles.RoleGUI;
import me.afarrukh.hashbot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

class ReactionManager {

    void sendToModifier(MessageReactionAddEvent evt) {
        RoleGUI rb = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).modifierForUser(evt.getUser());
        if (rb == null) {
            return;
        }
        rb.handleEvent(evt);
    }

    /**
     * We decide here if we want to post the message to the pinned channel depending on the reaction count
     * We are testing for distinct users, hence we use the Set class. Algorithm is unfortunately O(n^2)
     *
     * @param evt The event associated with the reaction being added
     */
    void processForPinning(MessageReactionAddEvent evt) {
        Message m = evt.getChannel().retrieveMessageById(evt.getMessageId()).complete();
        if (m == null) {
            return;
        }

        final String reactionId = "\uD83D\uDCCC"; // Pushpin emote ID

        // Getting the pinned channel ID from storage
        GuildDataManager gdm = GuildDataMapper.getInstance().getDataManager(evt.getGuild());

        if (gdm.isPinned(m.getId())) {
            return;
        }

        if (gdm.getPinnedChannelId() == null) {
            return;
        }

        TextChannel pinnedChannel = evt.getGuild().getTextChannelById(gdm.getPinnedChannelId());
        if (pinnedChannel == null) {
            return;
        }

        String pinnedChannelId = evt.getGuild().getTextChannelById(gdm.getPinnedChannelId()).getId();

        // Checking if the current channel is the pinned channel. If it is then we od not proceed
        if (pinnedChannelId.equals(evt.getChannel().getId())) {
            return;
        }

        // Checking if the pinned channel has been set
        MessageChannel channel = evt.getGuild().getTextChannelById(pinnedChannelId);
        if (channel == null) {
            return;
        }

        int size = 0;

        for (MessageReaction reaction : m.getReactions()) {
            if (reaction.getReactionEmote().getName().equals(reactionId)) {
                size = reaction.getCount();
                break;
            }
        }

        // If no reactions have been added then return
        if (size == 0) {
            return;
        }

        // If we haven't yet passed the threshold then return
        if (size < Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPinThreshold()) {
            return;
        }

        MessageUtils.pinMessage(m, channel);
    }
}
