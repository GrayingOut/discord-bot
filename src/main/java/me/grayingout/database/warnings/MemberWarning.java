package me.grayingout.database.warnings;

import java.time.LocalDateTime;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/**
 * A class which holds the data fetched from the warnings database
 * MemberWarning table
 */
public final class MemberWarning {
    
    /**
     * The unique identifier of the warning
     */
    private final int warningId;

    /**
     * The date and time the warning was received
     */
    private final LocalDateTime receivedAt;

    /**
     * The user id of the member that received the warning
     */
    private final long warnedUserId;

    /**
     * The user id of the member that gave the warning
     */
    private final long warnerUserId;

    /**
     * The reason of the warning
     */
    private final String reason;
    
    /**
     * Constructs a new {@code MemberWarning}
     * 
     * @param warningId    The id of the warning
     * @param receivedAt   The date and time the warning was received
     * @param warnedUserId The user id of the member that received the warning
     * @param warnerUserId The user id of the member that gave the warning
     * @param reason       The reason for the warning
     */
    public MemberWarning(int warningId, LocalDateTime receivedAt, long warnedUserId, long warnerUserId, String reason) {
        this.warningId = warningId;
        this.receivedAt = receivedAt;
        this.warnedUserId = warnedUserId;
        this.warnerUserId = warnerUserId;
        this.reason = reason;
    }

    /**
     * Get the warning id
     * 
     * @return The warning id
     */
    public int getWarningId() {
        return warningId;
    }

    /**
     * Get the date and time the warning was received
     * 
     * @return The date and time the warning was received
     */
    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    /**
     * Get the user id of the user warned
     * 
     * @return The user id of the warned user
     */
    public long getWarnedUserId() {
        return warnedUserId;
    }

    /**
     * Gets the warned member from the provided guild
     * 
     * @param guild The guild of the member
     * @return The member
     */
    public Member getWarnedMember(Guild guild) {
        return guild.getMemberById(warnedUserId);
    }

    /**
     * Gets the warned user
     * 
     * @param jda The JDA instance
     * @return The user
     */
    public User getWarnedUser(JDA jda) {
        return jda.getUserById(warnedUserId);
    }

    /**
     * Get the user id of the user who gave the warning
     * 
     * @return The user id of the warner user
     */
    public long getWarnerUserId() {
        return warnerUserId;
    }

    /**
     * Gets the warner member from the provided guild
     * 
     * @param guild The guild of the member
     * @return The member
     */
    public Member getWarnerMember(Guild guild) {
        return guild.getMemberById(warnerUserId);
    }

    /**
     * Gets the warner user
     * 
     * @param jda The JDA instance
     * @return The user
     */
    public User getWarnerUser(JDA jda) {
        return jda.getUserById(warnerUserId);
    }

    /**
     * Get the warning reason
     * 
     * @return The warning reason
     */
    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return String.format(
            "MemberWarning[%s, %s, %s, %s, %s]",
            warningId,
            receivedAt,
            warnedUserId,
            warnerUserId,
            reason
        );
    }
}
