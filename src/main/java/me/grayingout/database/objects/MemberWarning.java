package me.grayingout.database.objects;

import java.time.LocalDateTime;

import me.grayingout.App;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

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
     * The id of the guild the warning was in
     */
    private final long guildId;

    /**
     * The guild the warning was in
     */
    private final Guild guild;

    /**
     * The user id of the member that received the warning
     */
    private final long memberId;

    /**
     * The member that received the warning
     */
    private final Member member;

    /**
     * The user id of the member that gave the warning
     */
    private final long moderatorId;

    /**
     * The member that gave the warning
     */
    private final Member moderator;

    /**
     * The date and time the warning was received
     */
    private final LocalDateTime receivedAt;

    /**
     * The reason of the warning
     */
    private final String reason;
    
    /**
     * Constructs a new {@code MemberWarning}
     * 
     * @param warningId    The id of the warning
     * @param guildId      The guild the warning was in
     * @param receivedAt   The date and time the warning was received
     * @param warnedUserId The user id of the member that received the warning
     * @param warnerUserId The user id of the member that gave the warning
     * @param reason       The reason for the warning
     */
    public MemberWarning(int warningId, long guildId, long memberId, long moderatorId, LocalDateTime receivedAt, String reason) {
        this.warningId = warningId;
        this.guildId = guildId;
        this.memberId = memberId;
        this.moderatorId = moderatorId;
        this.receivedAt = receivedAt;
        this.reason = reason;

        this.guild = App.getBot().getJDA().getGuildById(guildId);
        this.member = this.guild.getMemberById(memberId);
        this.moderator = this.guild.getMemberById(moderatorId);
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
     * Get the user id of the member warned
     * 
     * @return The user id of the warned member
     */
    public long getMemberId() {
        return memberId;
    }

    /**
     * Gets the warned member
     * 
     * @return The member
     */
    public Member getMember(Guild guild) {
        return member;
    }

    /**
     * Get the user id of the user who gave the warning
     * 
     * @return The user id of the warner user
     */
    public long getModeratorId() {
        return moderatorId;
    }

    /**
     * Gets the warner member from the provided guild
     * 
     * @param guild The guild of the member
     * @return The member
     */
    public Member getModerator(Guild guild) {
        return moderator;
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
            "MemberWarning[warning_id=%s, guild_id=%s, member_id=%s, moderator_id=%s, received_at=%s, reason=\"%s\"]",
            warningId,
            guildId,
            memberId,
            moderatorId,
            receivedAt,
            reason
        );
    }
}
