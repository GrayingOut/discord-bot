package me.grayingout.database.entities;

import java.util.HashMap;

import me.grayingout.bot.MessageCache;
import me.grayingout.database.accessors.DatabaseAccessorManager;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
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

    /**
     * Creates a new {@code GuildLoggingChannel} for the
     * specific guild
     * 
     * @param guild     The guild
     * @param channelId The logging channel id
     */
    private GuildLoggingChannel(Guild guild, long channelId) {
        this.guild = guild;
        this.channelId = channelId;
        channel = guild.getChannelById(GuildMessageChannel.class, channelId);
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
            DatabaseAccessorManager.getConfigurationDatabaseAccessor().getLoggingChannelId(guild)
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
        channel = guild.getChannelById(GuildMessageChannel.class, channelId);
    }

    /**
     * Logs the deletion of a message
     * 
     * @param event          The deletion event
     */
    public final void logDeletedMessage(MessageDeleteEvent event) {
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
}
