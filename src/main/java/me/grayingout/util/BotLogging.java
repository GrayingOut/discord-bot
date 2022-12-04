package me.grayingout.util;

import me.grayingout.bot.events.MessageCache;
import me.grayingout.database.guildconfig.ConfigDatabase;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;

/**
 * A class used to log different bot actions to a guild's
 * logging channel
 */
public final class BotLogging {

    /**
     * Logs the deletion of a message
     * 
     * @param event          The deletion event
     */
    public static final void logDeletedMessage(MessageDeleteEvent event) {
        GuildMessageChannel loggingChannel = ConfigDatabase.getLoggingChannel(event.getGuild());
        if (loggingChannel == null) {
            return;
        }

        /* Ignore messages in logging channel */
        if (loggingChannel.getIdLong() == event.getChannel().getIdLong()) {
            return;
        }

        /* Check if the message deleted is in the message cache */
        if (MessageCache.getInstance().getMessageByIdLong(event.getMessageIdLong()) != null) {
            Message message = MessageCache.getInstance().getMessageByIdLong(event.getMessageIdLong());

            /* Log known message */
            loggingChannel.sendMessageEmbeds(EmbedFactory.createDeletedMessageLogEmbed(message)).queue();
            return;
        }

        /* Log unknown message */
        loggingChannel.sendMessageEmbeds(EmbedFactory.createUnknownDeletedMessageLogEmbed(
            event.getChannel(),
            event.getMessageIdLong()
        )).queue();
    }

    /**
     * Logs the usage of a bulk delete command
     * 
     * @param moderator The member that used the command
     * @param channel   The channel the messages were deleted from
     * @param count     The number of messages deleted
     */
    public static final void logBulkDeleteCommandUsage(Member moderator, GuildMessageChannel channel, int count) {
        GuildMessageChannel loggingChannel = ConfigDatabase.getLoggingChannel(channel.getGuild());
        if (loggingChannel == null) {
            return;
        }

        loggingChannel.sendMessageEmbeds(EmbedFactory.createBulkDeleteUsageEmbed(
            moderator,
            channel,
            count
        )).queue();
    }
}
