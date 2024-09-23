package me.afarrukh.hashbot.utils;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageUtils {
    public static void pinMessage(Database database, Message originalMessage, MessageChannel channel) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);

        String originalMessageContent = originalMessage.getContentRaw();

        // Special case for youtube videos since it prints the video URL twice if we don't check
        if (!originalMessageContent.contains("youtube.com")) {
            eb.appendDescription(originalMessageContent + "\n");
        }

        eb.setTitle(originalMessage.getMember().getEffectiveName());

        // Set the embed's image to be the attachment's image ONLY if it is an image
        if (!originalMessage.getAttachments().isEmpty()) {
            if (originalMessage.getAttachments().get(0).isImage()) {
                eb.setImage(originalMessage.getAttachments().get(0).getUrl());
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(originalMessage.getTimeCreated().toInstant().toEpochMilli());

        eb.setFooter(
                originalMessage.getChannel().getName() + " - " + calendar.getTime(),
                originalMessage.getAuthor().getAvatarUrl());

        // Just copy the original embed if there is one with our title and footer
        if (!originalMessage.getEmbeds().isEmpty()) {
            MessageEmbed firstEmbed = originalMessage.getEmbeds().get(0);
            if (firstEmbed.getImage() != null) eb.setImage(firstEmbed.getImage().getUrl());
            if (firstEmbed.getThumbnail() != null) {
                eb.setThumbnail(firstEmbed.getThumbnail().getUrl());
            }
            if (!firstEmbed.getFields().isEmpty()) {
                for (MessageEmbed.Field field : firstEmbed.getFields()) {
                    eb.addField(field);
                }
            }

            if (firstEmbed.getTitle() != null) {
                eb.appendDescription(firstEmbed.getTitle() + "\n");
            }

            eb.appendDescription(originalMessage.getContentRaw() + "\n");
            if (firstEmbed.getDescription() != null) {
                eb.appendDescription(firstEmbed.getDescription() + "\n");
            }
        }

        eb.appendDescription("\n[" + "Jump" + "](" + originalMessage.getJumpUrl() + ")");
        Message newMessage = channel.sendMessageEmbeds(eb.build()).complete();

        database.setMessageAsPinnedInGuild(
                originalMessage.getGuild().getId(), originalMessage.getId(), newMessage.getId());
    }

    public static void deleteAllMessagesFromBin(MessageReceivedEvent evt, List<Message> messageBin) {
        CompletableFuture.allOf(evt.getChannel().purgeMessages(messageBin).toArray(new CompletableFuture[0]))
                .join();
    }
}
