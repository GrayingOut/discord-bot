package me.grayingout.bot.logging;

import java.util.HashMap;

import me.grayingout.bot.events.MessageCache;
import me.grayingout.database.guildconfig.ConfigDatabase;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;

/**
 * A class used to log different bot actions to a guild's
 * logging channel
 */
public final class BotLoggingChannel {

    /**
     * Holds a cache of the {@code BotLoggingChannel} for a
     * guild
     */
    private static final HashMap<Long, BotLoggingChannel> botLoggingChannels = new HashMap<>();

    /**
     * The guild the logging channel belongs to
     */
    private final Guild guild;

    /**
     * The logging channel
     */
    private GuildMessageChannel loggingChannel;

    /**
     * Creates a new {@code BotLoggingChannel} for the
     * specific guild
     */
    private BotLoggingChannel(Guild guild) {
        this.guild = guild;
        loggingChannel = ConfigDatabase.getLoggingChannel(guild);
    }

    /**
     * Gets the {@code BotLoggingChannel} for a guild
     * 
     * @param guild The guild
     * @return The bot logging channel
     */
    public static final BotLoggingChannel getGuildBotLoggingChannel(Guild guild) {
        /* Check if already created a bot logging channel */
        if (botLoggingChannels.get(guild.getIdLong()) != null) {
            return botLoggingChannels.get(guild.getIdLong());
        }

        botLoggingChannels.put(guild.getIdLong(), new BotLoggingChannel(guild));

        return botLoggingChannels.get(guild.getIdLong());
    }

    /**
     * Refreshes the logging channel of a guild
     */
    public static final void refreshLoggingChannel(Guild guild) {
        if (botLoggingChannels.get(guild.getIdLong()) != null) {
            botLoggingChannels.get(guild.getIdLong()).refresh();
        }
    }


    /**
     * Refreshes the logging channel - internal use
     */
    public final void refresh() {
        loggingChannel = ConfigDatabase.getLoggingChannel(guild);
    }

    /**
     * Logs the deletion of a message
     * 
     * @param event          The deletion event
     */
    public final void logDeletedMessage(MessageDeleteEvent event) {
        if (loggingChannel == null) {
            return;
        }

        /* Ignore messages deleted in logging channel */
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
}
