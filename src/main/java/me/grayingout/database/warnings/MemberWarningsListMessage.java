package me.grayingout.database.warnings;

import java.util.concurrent.CompletableFuture;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

/**
 * A class used to store data fetched from the warnings
 * database MemberWarningsListMessage table
 */
public class MemberWarningsListMessage {
    
    /**
     * The id of the message
     */
    private final long messageId;

    /**
     * The user id of the member the warnings
     * list belongs to
     */
    private final long warnedUserId;

    /**
     * The current page being shown
     */
    private final int page;

    /**
     * Creates a new {@code MemberWarningsListMessage}
     * 
     * @param messageId    The id of the message
     * @param warnedUserId The id of the member the list belongs to
     * @param page         The current page
     */
    public MemberWarningsListMessage(long messageId, long warnedUserId, int page) {
        this.messageId = messageId;
        this.warnedUserId = warnedUserId;
        this.page = page;
    }

    /**
     * Gets the id of the message
     * 
     * @return The id of the message
     */
    public final long getMessageId() {
        return this.messageId;
    }

    /**
     * Gets the id of the warned user the list
     * belongs to
     * 
     * @return The id of the warned user
     */
    public final long getWarnedUserId() {
        return this.warnedUserId;
    }

    /**
     * Gets the current page being shown
     * 
     * @return The current page
     */
    public final int getCurrentPage() {
        return this.page;
    }

    /**
     * Returns a new completable future that is called
     * once the member has been retrieved
     * 
     * @param guild The guild
     * @returns A completable future of the member
     */
    public final CompletableFuture<Member> getWarnedMember(Guild guild) {
        CompletableFuture<Member> future = new CompletableFuture<>();

        guild.retrieveMemberById(warnedUserId).queue(member -> {
            future.complete(member);
        }, err -> {
            future.complete(null);
        });

        return future;
    }
}
