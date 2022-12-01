package me.grayingout.util;

import java.awt.Color;
import java.time.LocalDateTime;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

/**
 * A class used for making different types
 * of pre-made embeds
 */
public final class EmbedFactory {
    
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
