package me.grayingout.database.entities;

import java.util.HashMap;
import java.util.List;

import me.grayingout.bot.MessageCache;
import me.grayingout.database.accessors.DatabaseAccessorManager;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;

/**
 * A class used to log different bot actions to a guild's
 * logging channel
 */
public final class GuildLoggingChannel {

    /**
     * Holds a cache of the {@code GuildLoggingChannel} for a
     * guild
     */
    private static final HashMap<Long, GuildLoggingChannel> guildLoggingChannels = new HashMap<>();

    /**
     * The guild the logging channel belongs to
     */
    private final Guild guild;

    /**
     * The id of the logging channel
     */
    private long channelId;

    /**
     * The logging channel
     */
    private GuildMessageChannel channel;

    private List<LoggingEventType> enabledLoggingTypes;

    /**
     * Creates a new {@code GuildLoggingChannel} for the
     * specific guild
     * 
     * @param guild               The guild
     * @param channelId           The logging channel id
     * @param enabledLoggingTypes The logging types to log
     */
    private GuildLoggingChannel(Guild guild, long channelId, List<LoggingEventType> enabledLoggingTypes) {
        this.guild = guild;
        this.channelId = channelId;
        channel = guild.getChannelById(GuildMessageChannel.class, channelId);
        this.enabledLoggingTypes = enabledLoggingTypes;
    }

    /**
     * Gets the {@code GuildLoggingChannel} for a guild
     * 
     * @param guild The guild
     * @return The bot logging channel
     */
    public static final GuildLoggingChannel getGuildLoggingChannel(Guild guild) {
        /* Check if already created a bot logging channel */
        if (guildLoggingChannels.get(guild.getIdLong()) != null) {
            return guildLoggingChannels.get(guild.getIdLong());
        }

        guildLoggingChannels.put(guild.getIdLong(), new GuildLoggingChannel(
            guild,
            DatabaseAccessorManager.getConfigurationDatabaseAccessor().getLoggingChannelId(guild),
            DatabaseAccessorManager.getConfigurationDatabaseAccessor().getEnabledLoggingTypes(guild)
        ));

        return guildLoggingChannels.get(guild.getIdLong());
    }

    /**
     * Refreshes the logging channel of a guild
     */
    public static final void refreshLoggingChannel(Guild guild) {
        if (guildLoggingChannels.get(guild.getIdLong()) != null) {
            guildLoggingChannels.get(guild.getIdLong()).refresh();
        }
    }

    /**
     * Refreshes the logging channel - internal use
     */
    public final void refresh() {
        channelId = DatabaseAccessorManager.getConfigurationDatabaseAccessor().getLoggingChannelId(guild);
        enabledLoggingTypes = DatabaseAccessorManager.getConfigurationDatabaseAccessor().getEnabledLoggingTypes(guild);
        channel = guild.getChannelById(GuildMessageChannel.class, channelId);
    }

    /**
     * Returns if this logging channel is logging events of the type
     * specified
     * 
     * @param loggingEventType The type
     * @return If it is logging them
     */
    public final boolean isLoggingTypeEnabled(LoggingEventType loggingEventType) {
        return enabledLoggingTypes.contains(loggingEventType);
    }

    /**
     * Gets the channel logging messages are sent in
     * 
     * @return The channel, or {@code null} if no channel setup
     */
    public final GuildMessageChannel getLoggingChannel() {
        return channel;
    }

    /**
     * Logs the deletion of a message
     * 
     * @param event The deletion event
     */
    public final void logDeletedMessage(MessageDeleteEvent event) {
        if (!enabledLoggingTypes.contains(LoggingEventType.MESSAGE_DELETION_LOGGING)) {
            return;
        }

        if (channel == null) {
            return;
        }

        /* Ignore messages deleted in logging channel */
        if (channel.getIdLong() == event.getChannel().getIdLong()) {
            return;
        }

        /* Check if the message deleted is in the message cache */
        if (MessageCache.getInstance().getMessageByIdLong(event.getMessageIdLong()) != null) {
            Message message = MessageCache.getInstance().getMessageByIdLong(event.getMessageIdLong());

            /* Log known message */
            channel.sendMessageEmbeds(EmbedFactory.createDeletedMessageLogEmbed(message)).queue();
            return;
        }

        /* Log unknown message */
        channel.sendMessageEmbeds(EmbedFactory.createUnknownDeletedMessageLogEmbed(
            event.getChannel(),
            event.getMessageIdLong()
        )).queue();
    }

    /**
     * Logs the creation of a channel
     * 
     * @param event The create event
     */
    public final void logChannelCreate(ChannelCreateEvent event) {
        if (!enabledLoggingTypes.contains(LoggingEventType.CHANNEL_LOGGING)) {
            return;
        }

        if (channel == null) {
            return;
        }
        
        /* Log message */
        channel.sendMessageEmbeds(EmbedFactory.createChannelEventLogEmbed(event)).queue();
    }

    /**
     * Logs the deletion of a channel
     * 
     * @param event The deletion event
     */
    public final void logChannelDelete(ChannelDeleteEvent event) {
        if (!enabledLoggingTypes.contains(LoggingEventType.CHANNEL_LOGGING)) {
            return;
        }

        if (event.getChannel().getIdLong() == channelId) {
            /* Ignore if own channel deleted */
            return;
        }

        if (channel == null) {
            return;
        }
        
        /* Log message */
        channel.sendMessageEmbeds(EmbedFactory.createChannelEventLogEmbed(event)).queue();
    }

    /**
     * Logs the renaming of a channel
     * 
     * @param event The rename event
     */
    public final void logChannelNameChanges(ChannelUpdateNameEvent event) {
        if (!enabledLoggingTypes.contains(LoggingEventType.CHANNEL_LOGGING)) {
            return;
        }

        if (channel == null) {
            return;
        }

        /* Log message */
        channel.sendMessageEmbeds(EmbedFactory.createChannelEventLogEmbed(event)).queue();
    }

    /**
     * The type of logging event
     */
    public static enum LoggingEventType {
        /**
         * Log deleted messages
         */
        MESSAGE_DELETION_LOGGING,

        /**
         * Log channel events
         */
        CHANNEL_LOGGING;
    }
}
