package me.grayingout.database.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import me.grayingout.database.objects.GuildLoggingChannel;
import me.grayingout.database.objects.GuildWelcomeMessage;
import me.grayingout.database.query.DatabaseQuery;
import me.grayingout.util.WelcomeMessage;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

/**
 * Class for accessing the configuration data
 * of a guild in the config database
 */
public final class ConfigurationDatabaseAccessor extends DatabaseAccessor {
    
    /**
     * Creates a new {@code ConfigurationDatabaseAccessor}
     */
    public ConfigurationDatabaseAccessor() {
        super("configuration.db");
    }

    @Override
    public final void init() {
        queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                Statement statement = connection.createStatement();
                
                /* Creates the table that stores guild configurations */
                statement.execute(
                    "CREATE TABLE IF NOT EXISTS GuildConfiguration ("
                  + "  guild_id INTEGER NOT NULL PRIMARY KEY,"
                  + "  logging_channel_id INTEGER DEFAULT -1,"
                  + "  welcome_channel_id INTEGER DEFAULT -1,"
                  + "  welcome_message TEXT"
                  + ")");
                
                return null;
            }
        });
    }
    /**
     * Gets the welcome message used for a specific guild
     * 
     * @param guild   The guild
     */
    public final String getWelcomeMessage(Guild guild) {
        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<String>() {
            @Override
            public String execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT welcome_message FROM GuildConfiguration WHERE guild_id == ?"
                );

                statement.setLong(1, guild.getIdLong());

                /* No configuration in table */
                ResultSet set = statement.executeQuery();
                if (!set.next()) {
                    return WelcomeMessage.getDefaultWelcomeMessage();
                }

                /* No custom message */
                if (set.getString("welcome_message") == null) {
                    return WelcomeMessage.getDefaultWelcomeMessage();
                }

                return set.getString("welcome_message");
            }
        });

        try {
            return (String) future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Sets the welcome message used for a specific guild
     * 
     * @param guild   The guild
     * @param message The welcome message
     */
    public final void setWelcomeMessage(Guild guild, String message) {
        /* Make sure the guild has a config */
        createDefaultGuildConfig(guild);
        
        queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "UPDATE GuildConfiguration SET welcome_message = ? WHERE guild_id == ?"
                );

                statement.setString(1, message);
                statement.setLong(2, guild.getIdLong());

                statement.executeUpdate();

                return null;
            }
        }).thenAccept(o -> {
            /* Refresh the singleton */
            GuildWelcomeMessage.refreshWelcomeMessage(guild);
        });
    }

    /**
     * Gets the channel id of the channel that welcome
     * messages are sent in for a specific guild
     * 
     * @param guild The guild
     * @return The channel id
     */
    public final long getWelcomeChannelId(Guild guild) {
        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<Long>() {
            @Override
            public Long execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT welcome_channel_id FROM GuildConfiguration WHERE guild_id == ?"
                );

                statement.setLong(1, guild.getIdLong());

                ResultSet set = statement.executeQuery();
                if (!set.next()) {
                    return -1L;
                }

                return set.getLong("welcome_channel_id");
            }
        });

        try {
            return (long) future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    /**
     * Updates the welcome channel id for a specific guild
     * 
     * @param guild   The guild
     * @param channel The new welcome channel
     */
    public final void updateWelcomeChannelId(Guild guild, GuildMessageChannel channel) {
        /* Make sure the guild has a config row */
        createDefaultGuildConfig(guild);

        queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "UPDATE GuildConfiguration SET welcome_channel_id = ? WHERE guild_id == ?"
                );

                statement.setLong(1, channel.getIdLong());
                statement.setLong(2, guild.getIdLong());
    
                statement.executeUpdate();

                return null;
            }
        }).thenAccept(o -> {
            /* Refresh the singleton */
            GuildWelcomeMessage.refreshWelcomeMessage(guild);
        });
    }

    /**
     * Removes the welcome channel for a specific guild
     * 
     * @param guild   The guild
     */
    public final void removeWelcomeChannelId(Guild guild) {
        /* Make sure the guild has a config row */
        createDefaultGuildConfig(guild);

        queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "UPDATE GuildConfiguration SET welcome_channel_id = -1 WHERE guild_id == ?"
                );

                statement.setLong(1, guild.getIdLong());
    
                statement.executeUpdate();

                return null;
            }
        }).thenAccept(o -> {
            /* Refresh the singleton */
            GuildWelcomeMessage.refreshWelcomeMessage(guild);
        });
    }

    /**
     * Gets the logging channel id of a specific guild
     * 
     * @param guild The guild
     * @return The logging channel id, or {@code -1} if it does not exist
     */
    public final long getLoggingChannelId(Guild guild) {
        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<Long>() {
            @Override
            public Long execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT logging_channel_id FROM GuildConfiguration WHERE guild_id == ?"
                );

                statement.setLong(1, guild.getIdLong());
    
                ResultSet set = statement.executeQuery();
    
                if (!set.next()) {
                    return -1L;
                }
    
                return set.getLong("logging_channel_id");
            }
        });

        try {
            return (long) future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    /**
     * Updates the logging channel id of the specific guild
     * 
     * @param guild   The guild
     * @param channel The new logging channel
     */
    public final void updateLoggingChannelId(Guild guild, GuildMessageChannel channel) {
        /* Make sure the guild has a config row */
        createDefaultGuildConfig(guild);

        queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "UPDATE GuildConfiguration SET logging_channel_id = ? WHERE guild_id == ?"
                );

                statement.setLong(1, channel.getIdLong());
                statement.setLong(2, guild.getIdLong());
    
                statement.executeUpdate();

                return null;
            }
            
        }).thenAccept(o -> {
            /* Update the singleton */
            GuildLoggingChannel.refreshLoggingChannel(guild);
        });
    }

    /**
     * Checks if the guild has a config in the database, if
     * not it creates a default config
     */
    private final void createDefaultGuildConfig(Guild guild) {
        queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                /* Select the config */
                PreparedStatement checkStatement = connection.prepareStatement(
                    "SELECT * FROM GuildConfiguration WHERE guild_id == ?"
                );
                checkStatement.setLong(1, guild.getIdLong());

                ResultSet set = checkStatement.executeQuery();

                /* Check if a config already exists */
                if (set.next()) {
                    return null;
                }

                /* Insert the config */
                PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO GuildConfiguration (guild_id) VALUES (?)"
                );

                insertStatement.setLong(1, guild.getIdLong());

                insertStatement.executeUpdate();

                return null;
            }
        });
    }
}
