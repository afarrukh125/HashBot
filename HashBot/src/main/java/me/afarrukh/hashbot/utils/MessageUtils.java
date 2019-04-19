package me.afarrukh.hashbot.utils;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.util.Calendar;

/**
 * Created by Abdullah on 15/04/2019 19:36
 */
public class MessageUtils {

    /**
     * Pin a message based on an original message
     * Plenty of unpleasant code in this method, because of the way EmbedBuilder can return null values from JDA.
     *
     * @param originalMessage The original message to pin
     * @param channel         The channel to send the new pinned message to
     */
    public static void pinMessage(Message originalMessage, MessageChannel channel) {
        // Getting the pinned channel ID from storage
        GuildDataManager gdm = GuildDataMapper.getInstance().getDataManager(originalMessage.getGuild());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Constants.EMB_COL);

        eb.appendDescription(originalMessage.getContentRaw());
        eb.setTitle(originalMessage.getMember().getEffectiveName());

        // Set the embed's image to be the attachment's image ONLY if it is an image
        if (!originalMessage.getAttachments().isEmpty()) {
            if (originalMessage.getAttachments().get(0).isImage())
                eb.setImage(originalMessage.getAttachments().get(0).getUrl());
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(originalMessage.getCreationTime().toInstant().toEpochMilli());

        eb.setFooter(originalMessage.getChannel().getName() + " - " + calendar.getTime().toString()
                ,
                originalMessage.getAuthor().getAvatarUrl());

        // Just copy the original embed if there is one with our title and footer
        if (!originalMessage.getEmbeds().isEmpty()) {
            MessageEmbed firstEmbed = originalMessage.getEmbeds().get(0);
            if (firstEmbed.getImage() != null)
                eb.setImage(firstEmbed.getImage().getUrl());
            if (firstEmbed.getThumbnail() != null)
                eb.setThumbnail(firstEmbed.getThumbnail().getUrl());
            if (!firstEmbed.getFields().isEmpty()) {
                for (MessageEmbed.Field field : firstEmbed.getFields())
                    eb.addField(field);
            }

            if (firstEmbed.getTitle() != null)
                eb.appendDescription(firstEmbed.getTitle() + "\n");

            eb.appendDescription(originalMessage.getContentRaw() + "\n");
            if (firstEmbed.getDescription() != null)
                eb.appendDescription(firstEmbed.getDescription());
        }

        Message newMessage = channel.sendMessage(eb.build()).complete();

        gdm.addAsPinned(originalMessage.getId(), newMessage.getId());
    }
}