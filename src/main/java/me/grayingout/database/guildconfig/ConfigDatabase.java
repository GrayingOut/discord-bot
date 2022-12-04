package me.grayingout.database.guildconfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

/**
 * Class for accessing the configuration data
 * of a guild in the config database
 */
public final class ConfigDatabase {

    /**
     * The db path used for connecting
     */
    private static final String dbPath = "jdbc:sqlite:configurations.db";

    /**
     * The connection to the warnings db
     */
    private static Connection dbConnection;
    
    /**
     * Connect to the database
     */
    public static final void connect() {
        /* Open connection */
        try {
            dbConnection = DriverManager.getConnection(dbPath);
        } catch (SQLException e) {
            System.err.println("Failed to connect to configurations DB");
            e.printStackTrace();
            return;
        }

        /* Initialise table */
        try (Statement statement = dbConnection.createStatement()) {
            /* Creates the table that stores member warnings */
            statement.execute(
                  "CREATE TABLE IF NOT EXISTS GuildConfiguration ("
                + "  guild_id INTEGER NOT NULL PRIMARY KEY,"
                + "  logging_channel_id INTEGER DEFAULT -1"
                + ")");
        } catch (SQLException e) {
            System.err.println("Failed to initialise configurations DB");
            e.printStackTrace();
            return;
        }
    }

    /**
     * Returns if there is an active connection to the warnings
     * database
     * 
     * @return If there is an active connection
     */
    public static final boolean isConnected() {
        return dbConnection != null;
    }

    /**
     * Gets the logging channel id of a specific guild
     * 
     * @param guild The guild
     * @return The logging channel id, or {@code -1} if it does not exist or an error occurred
     */
    public static final long getLoggingChannelId(Guild guild) {
        try (PreparedStatement statement = dbConnection.prepareStatement(
            "SELECT logging_channel_id FROM GuildConfiguration WHERE guild_id == ?"
        )) {
            statement.setLong(1, guild.getIdLong());

            ResultSet set = statement.executeQuery();

            if (!set.next()) {
                return -1;
            }

            return set.getLong("logging_channel_id");
        } catch (SQLException e) {
            System.err.println("Failed to get guild logging channel id");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Gets the logging channel of a specific guild
     * 
     * @param guild The guild
     * @return The channel, or {@code null} if not channel exists
     */
    public static final GuildMessageChannel getLoggingChannel(Guild guild) {
        return guild.getChannelById(GuildMessageChannel.class, getLoggingChannelId(guild));
    }

    /**
     * Gets the logging channel id of the specific guild
     * 
     * @param guild   The guild
     * @param channel The logging channel
     * @return If the operation was successful
     */
    public static final boolean updateLoggingChannelId(Guild guild, GuildMessageChannel channel) {
        if (!createDefaultGuildConfig(guild)) {
            return false;
        }

        try (PreparedStatement statement = dbConnection.prepareStatement(
            "UPDATE GuildConfiguration SET logging_channel_id = ? WHERE guild_id == ?"
        )) {
            statement.setLong(1, channel.getIdLong());
            statement.setLong(2, guild.getIdLong());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to update guild logging channel id");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Checks if the guild has a config in the database, if
     * not it creates a default config
     * 
     * @return If the operation was successful
     */
    private static final boolean createDefaultGuildConfig(Guild guild) {

        /**
         * A transaction is used because two queries are required: one to
         * check a config already exists and one to create it. A transaction
         * prevents a race condition
         */
        try (Connection transactionConnection = DriverManager.getConnection(dbPath)) {
            transactionConnection.setAutoCommit(false);

            /* Select the config */
            PreparedStatement checkStatement = transactionConnection.prepareStatement(
                "SELECT * FROM GuildConfiguration WHERE guild_id == ?"
            );
            checkStatement.setLong(1, guild.getIdLong());

            ResultSet set = checkStatement.executeQuery();

            /* Check if a config already exists */
            if (set.next()) {
                return true;
            }

            /* Insert the config */
            PreparedStatement insertStatement = transactionConnection.prepareStatement(
                "INSERT INTO GuildConfiguration (guild_id) VALUES (?)"
            );

            insertStatement.setLong(1, guild.getIdLong());

            insertStatement.execute();

            /* Commit the transaction */
            transactionConnection.commit();
        } catch (SQLException e) {
            System.err.println("Failed to create default guild config");
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
}
