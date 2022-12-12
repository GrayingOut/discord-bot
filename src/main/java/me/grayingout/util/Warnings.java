package me.grayingout.util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

/**
 * Utility methods for the warnings system
 */
public final class Warnings {

    /**
     * Creates the warning embed sent to a warned user's DMs
     * 
     * @param member The member warned
     * @return The embed
     */
    public static final MessageEmbed createDMWarningEmbed(Member member, Member moderator, String reason) {
        Field[] fields = {
            new Field("Moderator", moderator.getUser().getAsTag() + " (" + moderator.getId() + ")", false),
            new Field("Reason", reason, false)
        };

        MessageEmbed embed = EmbedFactory.createWarningEmbed(
            "You received a warning in " + member.getGuild().getName() + " (" + moderator.getGuild().getId() + ")",
            "",
            fields
        );
        
        return embed;
    }

    /**
     * Creates the warning embed sent as a response to the warn
     * command
     * 
     * @return The embed
     */
    public static final MessageEmbed createWarningSuccessEmbed(Member member, Member moderator, String reason, int warningId) {
        Field[] fields = {
            new Field("Member", String.format(
                "%s (%s)",
                member.getUser().getAsTag(),
                member.getIdLong()), false),
            new Field("Moderator", String.format(
                "%s (%s)",
                moderator.getUser().getAsTag(),
                moderator.getIdLong()), false),
            new Field("Reason", reason, false),
            new Field("Warning Id", Integer.toString(warningId), false)
        };

        return EmbedFactory.createSuccessEmbed(
            "Member Warned",
            member.getUser().getAsTag() + " (" + member.getId() + ") has been warned",
            fields
        );
    }

    /**
     * Creates a warning embed that the user was not found
     * 
     * @return The embed
     */
    public static final MessageEmbed createMemberNotFoundEmbed() {
        return EmbedFactory.createWarningEmbed(
            "Member not Found",
            "The requested member was not found in the guild - maybe they left"
        );
    }
}
