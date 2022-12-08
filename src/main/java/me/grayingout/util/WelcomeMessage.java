package me.grayingout.util;

import me.grayingout.database.entities.GuildWelcomeMessage;
import net.dv8tion.jda.api.entities.Member;

/**
 * Utility methods for welcome messages
 */
public final class WelcomeMessage {
    
    /**
     * Returns the formatted version of the welcome message
     * 
     * @param welcomeMessage The welcome message config
     * @param member         The member that joined
     * @return The formatted welcome message
     */
    public static final String formatWelcomeMessage(GuildWelcomeMessage welcomeMessage, Member member) {
        return welcomeMessage
            .getMessage()
            .replaceAll("\\{user\\.mention\\}", member.getAsMention())
            .replaceAll("\\{user\\.tag\\}", member.getUser().getAsTag())
            .replaceAll("\\{user\\.name\\}", member.getUser().getName())
            .replaceAll("\\{guild\\.name\\}", welcomeMessage.getGuild().getName());
    }

    /**
     * Gets the default welcome message for when none
     * is set
     * 
     * @return The message
     */
    public static final String getDefaultWelcomeMessage() {
        return ":wave: Welcome {user.mention} to {guild.name}. Enjoy your stay!";
    }
}
