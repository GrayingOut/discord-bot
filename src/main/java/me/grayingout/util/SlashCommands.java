package me.grayingout.util;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Utility methods for slash commands
 */
public final class SlashCommands {
    
    /**
     * Safely gets an integer option from a slash command interaction
     * event. It either returns the value of the option or {@code null}
     * if the integer is invalid
     * 
     * @param event      The slash command interaction event
     * @param optionName The name of the int option
     * @return The int value, or {@code null} if invalid
     */
    public static final Integer safelyGetIntOption(SlashCommandInteractionEvent event, String optionName) {
        try {
            return event.getOption(optionName).getAsInt();
        } catch (ArithmeticException e) {
            return null;
        }
    }
}
