package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

class ReactionManager {

    /**
     * We decide here if we want to post the message to the pinned channel depending on the reaction count
     * We are testing for distinct users, hence we use the Set class. Algorithm is unfortunately O(n^2)
     *
     * @param evt The event associated with the reaction being added
     */
    void processForPinning(MessageReactionAddEvent evt) {
        Message message =
                evt.getChannel().retrieveMessageById(evt.getMessageId()).complete();
        if (message == null) {
            return;
        }

        final String reactionId = "\uD83D\uDCCC"; // Pushpin emote ID

        Database database = Database.getInstance();

        if (database.isBotPinMessageInGuild(evt.getGuild().getId(), message.getId())) {
            return;
        }

        database.getPinnedChannelIdForGuild(evt.getGuild().getId()).ifPresent(pinnedChannelId -> {
            TextChannel pinnedChannel = evt.getGuild().getTextChannelById(pinnedChannelId);
            if (pinnedChannel == null) {
                return;
            }

            if (pinnedChannelId.equals(evt.getChannel().getId())) {
                return;
            }

            MessageChannel channel = evt.getGuild().getTextChannelById(pinnedChannelId);
            if (channel == null) {
                return;
            }

            int size = 0;

            for (MessageReaction reaction : message.getReactions()) {
                if (reaction.getEmoji().getName().equals(reactionId)) {
                    size = reaction.getCount();
                    break;
                }
            }

            if (size == 0) {
                return;
            }

            if (size < database.getPinThresholdForGuild(evt.getGuild().getId())) {
                return;
            }

            if (database.isMessagePinnedInGuild(evt.getGuild().getId(), evt.getMessageId())) {
                return;
            }

            MessageUtils.pinMessage(message, channel);
        });
    }
}
