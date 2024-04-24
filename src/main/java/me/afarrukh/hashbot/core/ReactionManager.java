package me.afarrukh.hashbot.core;

import com.google.inject.Inject;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ReactionManager {

    private final Database database;

    @Inject
    public ReactionManager(Database database) {
        this.database = database;
    }

    void processForPinning(MessageReactionAddEvent evt) {
        Message message =
                evt.getChannel().retrieveMessageById(evt.getMessageId()).complete();
        if (message == null) {
            return;
        }

        final String reactionId = "\uD83D\uDCCC"; // Pushpin emote ID


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

            MessageUtils.pinMessage(database, message, channel);
        });
    }
}
