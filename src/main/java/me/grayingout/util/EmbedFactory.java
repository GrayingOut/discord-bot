package me.grayingout.util;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.channel.Channel;

/**
 * A class used for making different types
 * of pre-made embeds
 */
public final class EmbedFactory {
    
    /**
     * Creates an embed for logging a deleted message
     * 
     * @param message The deleted message
     * @return The built embed, and any embeds on the message
     */
    public static final Collection<MessageEmbed> createDeletedMessageLogEmbed(Message message) {
        List<MessageEmbed> embeds = new ArrayList<>();
        
        /* Build deleted message embed */
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.RED)
            .setTitle("✂ **Deleted Message in " + message.getChannel().getAsMention() + "**")
            .addField("Channel", message.getChannel().getAsMention() + " (" + message.getChannel().getId() + ")", false)
            .addField("Author", message.getAuthor().getAsMention() + " (" + message.getAuthor().getId() + ")", false)
            .addField("Content", message.getContentRaw(), false)
            .setFooter(message.getId())
            .setTimestamp(LocalDateTime.now());
        
        /* Add attachments */
        StringBuilder attachments = new StringBuilder();
        for (Attachment attachment : message.getAttachments()) {
            attachments.append(attachment.getProxyUrl() + "\n");
        }
        builder.addField("Attachments", attachments.toString(), false);

        /* Create list of embeds */
        embeds.add(builder.build());
        embeds.addAll(message.getEmbeds());

        return embeds;
    }

    /**
     * Creates an embed for logging a deleted message that wasn't
     * in the {@code MessageCache}
     * 
     * @param channel   The channel the message was deleted from
     * @param messageId The id of the deleted message
     * @return The built embed
     */
    public static final Collection<MessageEmbed> createUnknownDeletedMessageLogEmbed(Channel channel, long messageId) {
        List<MessageEmbed> embeds = new ArrayList<>();

        /* Build deleted message embed */
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.RED)
            .setTitle("✂ **Deleted Message in " + channel.getAsMention() + "**")
            .setDescription(":warning: A message was deleted that was not stored in the bot's message cache. Limited data is available.")
            .addField("Channel", channel.getAsMention() + " (" + channel.getId() + ")", false)
            .setFooter(Long.toString(messageId))
            .setTimestamp(LocalDateTime.now());
        
        /* Create list of embeds */
        embeds.add(builder.build());

        return embeds;
    }

    /**
     * An embed used when an exception occurs and the
     * bot cannot proceed any further
     * 
     * @param e       The exception
     * @param message The message shown to the user
     * @returns The built embed
     */
    public static final MessageEmbed createExceptionEmbed(Throwable t, String message) {
        MessageEmbed embed = new EmbedBuilder()
            .setColor(Color.RED)
            .setTitle("**:red_circle: " + t.getClass().getSimpleName() + "**")
            .setDescription(message)
            .setTimestamp(LocalDateTime.now())
            .build();
        
        return embed;
    }

    /**
     * An embed used when the bot cannot complete the operation
     * but no error has been thrown
     * 
     * @param title   The title of the warning
     * @param message The message shown to the user
     * @return The built embed
     */
    public static final MessageEmbed createWarningEmbed(String title, String message) {
        MessageEmbed embed = new EmbedBuilder()
            .setColor(Color.ORANGE)
            .setTitle("**:warning: " + title + "**")
            .setDescription(message)
            .setTimestamp(LocalDateTime.now())
            .build();
        
        return embed;
    }

    /**
     * An embed used when the bot successfully completed an
     * operation with the option for fields
     * 
     * @param title   The title of the success
     * @param message The message shown to the user
     * @param fields  Any embed fields to add
     * @return The built embed
     */
    public static final MessageEmbed createSuccessEmbed(String title, String message, Field... fields) {
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.GREEN.darker())
            .setTitle("**:white_check_mark: " + title + "**")
            .setDescription(message)
            .setTimestamp(LocalDateTime.now());
        
        /* Add fields */
        for (Field field : fields) {
            builder.addField(field);
        }
        
        return builder.build();
    }

    /**
     * An embed used when the bot successfully completed an
     * operation
     * 
     * @param title   The title of the success
     * @param message The message shown to the user
     * @return The built embed
     */
    public static final MessageEmbed createSuccessEmbed(String title, String message) {
        return createSuccessEmbed(title, message, new Field[] {});
    }

    /**
     * An embed used to send a generic message to the user with
     * the option for fields
     * 
     * @param title   The title of the message
     * @param message The message shown to the user
     * @param fields  Any embed fields to add
     * @return The built embed
     */
    public static final MessageEmbed createGenericEmbed(String title, String message, Field... fields) {
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.BLUE)
            .setTitle(title)
            .setDescription(message)
            .setTimestamp(LocalDateTime.now());
        
        /* Add fields */
        for (Field field : fields) {
            builder.addField(field);
        }

        return builder.build();
    }

    /**
     * An embed used to send a generic message to the user
     * 
     * @param title   The title of the message
     * @param message The message shown to the user
     * @return The built embed
     */
    public static final MessageEmbed createGenericEmbed(String title, String message) {
        return createGenericEmbed(title, message, new Field[] {});
    }
}
