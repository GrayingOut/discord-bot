package me.grayingout.database.accessors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import me.grayingout.database.entities.GuildLoggingChannel;
import me.grayingout.database.entities.GuildLoggingChannel.LoggingEventType;
import me.grayingout.database.entities.GuildWelcomeMessage;
import me.grayingout.database.query.DatabaseQuery;
import me.grayingout.util.WelcomeMessage;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
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
                  + "  enabled_logging_types TEXT DEFAULT \"\","
                  + "  welcome_channel_id INTEGER DEFAULT -1,"
                  + "  welcome_message TEXT,"
                  + "  dj_role_id INTEGER DEFAULT -1"
                  + ")");
                
                return null;
            }
        });
    }

    /**
     * Removes the DJ role in a guild
     * 
     * @param guild The guild
     */
    public final void removeGuildDJRole(Guild guild) {

        queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "UPDATE GuildConfiguration SET dj_role_id = -1 WHERE guild_id == ?"
                );

                statement.setLong(1, guild.getIdLong());

                statement.executeUpdate();

                return null;
            }
        });
    }

    /**
     * Updates the DJ role in a guild
     * 
     * @param guild The guild
     * @param role  The role to use
     */
    public final void updateGuildDJRole(Guild guild, Role role) {
        /* Make sure guild has a config */
        createDefaultGuildConfig(guild);

        queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "UPDATE GuildConfiguration SET dj_role_id = ? WHERE guild_id == ?"
                );

                statement.setLong(1, role.getIdLong());
                statement.setLong(2, guild.getIdLong());

                statement.executeUpdate();

                return null;
            }
        });
    }

    /**
     * Gets the DJ role in a guild or {@code null} if
     * there is no DJ role
     * 
     * @param guild The guild
     * @return The DJ role, or {@code null}
     */
    public final Role getGuildDJRole(Guild guild) {
        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<Long>() {
            @Override
            public Long execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT dj_role_id FROM GuildConfiguration WHERE guild_id == ?"
                );

                statement.setLong(1, guild.getIdLong());

                ResultSet set = statement.executeQuery();

                if (!set.next()) {
                    return -1L;
                }

                return set.getLong("dj_role_id");
            }
        });

        return guild.getRoleById((long) future.join());
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

        return (String) future.join();
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
        
        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<Void>() {
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
        });

        /* Wait for query to finish */
        future.join();
        
        /* Update the welcome message singleton */
        GuildWelcomeMessage.refreshWelcomeMessage(guild);
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

        return (long) future.join();
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

        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<Void>() {
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
        });

        /* Wait for query to finish */
        future.join();
        
        /* Update the welcome message singleton */
        GuildWelcomeMessage.refreshWelcomeMessage(guild);
    }

    /**
     * Removes the welcome channel for a specific guild
     * 
     * @param guild   The guild
     */
    public final void removeWelcomeChannelId(Guild guild) {
        /* Make sure the guild has a config row */
        createDefaultGuildConfig(guild);

        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "UPDATE GuildConfiguration SET welcome_channel_id = -1 WHERE guild_id == ?"
                );

                statement.setLong(1, guild.getIdLong());
    
                statement.executeUpdate();

                return null;
            }
        });

        /* Wait for query to finish */
        future.join();
        
        /* Update the welcome message singleton */
        GuildWelcomeMessage.refreshWelcomeMessage(guild);
    }

    /**
     * Enables a specific logging type in a guild
     * 
     * @param guild THe guild
     * @param type The logging type
     */
    public final void enableLoggingType(Guild guild, GuildLoggingChannel.LoggingEventType type) {
        /* Make sure guild has a config row */
        createDefaultGuildConfig(guild);
        
        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT enabled_logging_types FROM GuildConfiguration WHERE guild_id == ?"
                );

                selectStatement.setLong(1, guild.getIdLong());

                /* Check a result set did come back - it should */
                ResultSet set = selectStatement.executeQuery();
                if (!set.next()) {
                    return null;
                }

                /* Ignore if already enabled */
                String currentLoggingTypes = set.getString("enabled_logging_types");
                if (currentLoggingTypes.indexOf(type.name()) != -1) {
                    return null;
                }

                /* Add the new type */
                String newLoggingTypes = currentLoggingTypes + "," + type.name();

                /* Update field */
                PreparedStatement updateStatement = connection.prepareStatement(
                    "UPDATE GuildConfiguration SET enabled_logging_types = ? WHERE guild_id == ?"
                );

                updateStatement.setString(1, newLoggingTypes);
                updateStatement.setLong(2, guild.getIdLong());

                updateStatement.executeUpdate();

                return null;
            }
        });

        /* Wait for query to finish */
        future.join();

        /* Update guild logging channel */
        GuildLoggingChannel.refreshLoggingChannel(guild);
    }

    /**
     * Disables a specific logging type in a guild
     * 
     * @param guild The guild
     * @param type The logging type
     */
    public final void disableLoggingType(Guild guild, GuildLoggingChannel.LoggingEventType type) {
        /* Make sure guild has a config row */
        createDefaultGuildConfig(guild);
        
        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT enabled_logging_types FROM GuildConfiguration WHERE guild_id == ?"
                );

                selectStatement.setLong(1, guild.getIdLong());

                /* Check a result set did come back - it should */
                ResultSet set = selectStatement.executeQuery();
                if (!set.next()) {
                    return null;
                }

                /* Remove the type */
                String currentLoggingTypes = set.getString("enabled_logging_types");
                String newLoggingTypes = currentLoggingTypes.replace(type.name(), "").replace(",,", ",");

                /* Update field */
                PreparedStatement updateStatement = connection.prepareStatement(
                    "UPDATE GuildConfiguration SET enabled_logging_types = ? WHERE guild_id == ?"
                );

                updateStatement.setString(1, newLoggingTypes);
                updateStatement.setLong(2, guild.getIdLong());

                updateStatement.executeUpdate();

                return null;
            }
        });

        /* Wait for query to finish */
        future.join();

        /* Update guild logging channel */
        GuildLoggingChannel.refreshLoggingChannel(guild);
    }

    /**
     * Gets the enabled logging types for a guild
     * 
     * @param guild The guild
     * @return The enabled logging types
     */
    public final List<GuildLoggingChannel.LoggingEventType> getEnabledLoggingTypes(Guild guild) {
        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<String>() {
            @Override
            public String execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT enabled_logging_types FROM GuildConfiguration WHERE guild_id == ?"
                );

                statement.setLong(1, guild.getIdLong());

                ResultSet set = statement.executeQuery();
                if (!set.next()) {
                    return "";
                }

                return set.getString("enabled_logging_types");
            }
        });

        /* Wait for query to finish */
        List<String> enabledLoggingTypesStrings = new ArrayList<String>(Arrays.asList(((String) future.join()).split(",")));
        List<LoggingEventType> enabledLoggingEventTypes = new ArrayList<>();
        
        /* Get the logging event types which are enabled */
        for (LoggingEventType loggingEventType : GuildLoggingChannel.LoggingEventType.values()) {
            if (enabledLoggingTypesStrings.contains(loggingEventType.toString())) {
                enabledLoggingEventTypes.add(loggingEventType);
            }
        }

        return enabledLoggingEventTypes;
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

        return (long) future.join();
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

        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<Void>() {
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
            
        });

        /* Wait for query to finish */
        future.join();
        
        /* Update the logging channel singleton */
        GuildLoggingChannel.refreshLoggingChannel(guild);
    }

    /**
     * Remove a logging channel from a guild
     * 
     * @param guild The guild
     */
    public final void removeLoggingChannel(Guild guild) {
        /* Make sure the guild has a config row */
        createDefaultGuildConfig(guild);

        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "UPDATE GuildConfiguration SET logging_channel_id = ? WHERE guild_id == ?"
                );

                statement.setLong(1, -1);
                statement.setLong(2, guild.getIdLong());

                statement.executeUpdate();

                return null;
            }
        });

        /* Wait for query to finish */
        future.join();
        
        /* Update the logging channel singleton */
        GuildLoggingChannel.refreshLoggingChannel(guild);
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
