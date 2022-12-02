package me.grayingout.database.warnings;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

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
     * The message
     */
    private final Message message;

    /**
     * The id of the channel the message was sent in
     */
    private final long channelId;

    /**
     * The channel the message was sent in
     */
    private final MessageChannel channel;

    /**
     * The id of the guild the message was sent in
     */
    private final long guildId;
    
    /**
     * The guild the message was sent in
     */
    private final Guild guild;

    /**
     * The user id of the member the warnings
     * list belongs to
     */
    private final long warnedUserId;

    /**
     * The member warned
     */
    private final Member warnedMember;

    /**
     * The current page being shown
     */
    private final int page;

    /**
     * Creates a new {@code MemberWarningsListMessage}
     * 
     * @param jda          The JDA instance
     * @param messageId    The id of the message
     * @param channelId    The id of the channel the message was sent in
     * @param guildId      The id the guild the message was sent in
     * @param warnedUserId The id of the member the list belongs to
     * @param page         The current page
     */
    public MemberWarningsListMessage(JDA jda, long messageId, long channelId, long guildId, long warnedUserId, int page) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.guildId = guildId;
        this.warnedUserId = warnedUserId;
        this.page = page;
        this.guild = jda.getGuildById(guildId);
        this.warnedMember = this.guild.getMemberById(warnedUserId);
        this.channel = this.guild.getChannelById(MessageChannel.class, channelId);
        this.message = this.channel.retrieveMessageById(messageId).complete();
    }

    /**
     * Gets the id of the message
     * 
     * @return The id of the message
     */
    public final long getMessageId() {
        return messageId;
    }

    /**
     * Gets the message from the message id
     * 
     * @return The message
     */
    public final Message getMessage() {
        return message;
    }

    /**
     * Gets the id of the channel the message was sent in
     * 
     * @return The channel id
     */
    public final long getChannelId() {
        return channelId;
    }

    /**
     * Gets the channel the message was sent in
     * 
     * @return The channel
     */
    public final MessageChannel getChannel() {
        return channel;
    }

    /**
     * Gets the id of the guild the message was sent in
     * 
     * @return The guild id
     */
    public final long getGuildId() {
        return guildId;
    }

    /**
     * Gets the guild the message was sent in
     * 
     * @return The guild
     */
    public final Guild getGuild() {
        return guild;
    }

    /**
     * Gets the id of the user the list belongs to
     * 
     * @return The user id
     */
    public final long getWarnedUserId() {
        return warnedUserId;
    }
    
    /**
     * Gets the guild specific member the list
     * belongs to
     * 
     * @returns The guild specific member
     */
    public final Member getWarnedMember() {
        return warnedMember;
    }

    /**
     * Gets the current page being shown
     * 
     * @return The current page
     */
    public final int getCurrentPage() {
        return page;
    }
}
