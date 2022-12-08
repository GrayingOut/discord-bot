package me.grayingout.database.entities;

import me.grayingout.App;
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
     * The user id of the warnings list member
     */
    private final long memberId;

    /**
     * The warnings list member
     */
    private final Member member;

    /**
     * The current page being shown
     */
    private final int page;

    /**
     * Creates a new {@code MemberWarningsListMessage}
     * 
     * @param messageId    The id of the message
     * @param channelId    The id of the channel the message was sent in
     * @param guildId      The id of the guild the message was sent in
     * @param memberId     The id of the member whose warnings are showing
     * @param page         The current page
     */
    public MemberWarningsListMessage(long messageId, long channelId, long guildId, long memberId, int page) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.guildId = guildId;
        this.memberId = memberId;
        this.page = page;

        this.guild = App.getBot().getJDA().getGuildById(guildId);
        this.member = this.guild.getMemberById(memberId);
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
     * Gets the id of the member the list belongs to
     * 
     * @return The member id
     */
    public final long getMemberId() {
        return memberId;
    }
    
    /**
     * Gets the guild specific member the list
     * belongs to
     * 
     * @returns The guild specific member
     */
    public final Member getMember() {
        return member;
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
