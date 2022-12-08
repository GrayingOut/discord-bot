package me.grayingout.database.entities;

import java.util.HashMap;

import me.grayingout.database.accessors.DatabaseAccessorManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

/**
 * Holds data from the configuration database on a
 * guilds welcome message setup and helper methods
 */
public final class GuildWelcomeMessage {

    /**
     * Store the GuildWelcomeMessage singletons for each guild
     * against its id
     */
    private static final HashMap<Long, GuildWelcomeMessage> welcomeMessages = new HashMap<>();

    /**
     * The guild this welcome message belongs to
     */
    private final Guild guild;

    /**
     * The id of the welcome channel
     */
    private long channelId;

    /**
     * The welcome channel
     */
    private GuildMessageChannel channel;
    
    /**
     * The welcome message
     */
    private String message;

    /**
     * Creates a new {@code GuildWelcomeMessage}
     * 
     * @param guild     The guild this object belongs to
     * @param channelId The id of the welcome message channel
     * @param message   The welcome message
     */
    private GuildWelcomeMessage(Guild guild, long channelId, String message) {
        this.guild = guild;
        this.channelId = channelId;
        this.channel = guild.getChannelById(GuildMessageChannel.class, channelId);
        this.message = message;
    }

    /**
     * Gets the singleton {@code GuildWelcomeMessage} instance for
     * a guild
     * 
     * @param guild The guild
     * @return The singleton
     */
    public static GuildWelcomeMessage getGuildWelcomeMessage(Guild guild) {
        /* Check singleton exists */
        if (welcomeMessages.get(guild.getIdLong()) == null) {
            welcomeMessages.put(guild.getIdLong(), new GuildWelcomeMessage(
                guild,
                DatabaseAccessorManager.getConfigurationDatabaseAccessor().getWelcomeChannelId(guild),
                DatabaseAccessorManager.getConfigurationDatabaseAccessor().getWelcomeMessage(guild)
            ));
        }

        return welcomeMessages.get(guild.getIdLong());
    }

    /**
     * Refreshes the data inside a specific guild singleton
     * 
     * @param guild THe guild to refresh
     */
    public static void refreshWelcomeMessage(Guild guild) {
        if (welcomeMessages.get(guild.getIdLong()) != null) {
            welcomeMessages.get(guild.getIdLong()).refresh();
        }
    }

    /**
     * Refreshes the data within this singleton
     */
    public final void refresh() {
        channelId = DatabaseAccessorManager.getConfigurationDatabaseAccessor().getLoggingChannelId(guild);
        channel = guild.getChannelById(GuildMessageChannel.class, channelId);
        message = DatabaseAccessorManager.getConfigurationDatabaseAccessor().getWelcomeMessage(guild);
    }

    /**
     * Gets the guild this welcome message belongs to
     * 
     * @return The guild
     */
    public final Guild getGuild() {
        return this.guild;
    }

    /**
     * Gets the id of the welcome channel
     * 
     * @return The channel id
     */
    public final long getWelcomeChannelId() {
        return this.channelId;
    }

    /**
     * Gets the welcome channel
     * 
     * @return The channel
     */
    public final GuildMessageChannel getWelcomeChannel() {
        return this.channel;
    }

    /**
     * Gets the raw welcome message
     * 
     * @return The message
     */
    public final String getMessage() {
        return this.message;
    }
}
