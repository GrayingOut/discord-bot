package me.grayingout.database.warnings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

/*
 * Class for accessing and updating information
 * in the warnings sqlite database
 */
public final class WarningsDatabase {

    /**
     * The db path used for connecting
     */
    private static final String dbPath = "jdbc:sqlite:warnings.db";

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
            System.err.println("Failed to connect to warnings DB");
            e.printStackTrace();
            return;
        }

        /* Initialise table */
        try (Statement statement = dbConnection.createStatement()) {
            /* Creates the table that stores member warnings */
            statement.execute(
                  "CREATE TABLE IF NOT EXISTS MemberWarning ("
                + "  warning_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                + "  received_at INTEGER NOT NULL,"
                + "  warned_user_id INTEGER NOT NULL,"
                + "  warner_user_id INTEGER NOT NULL,"
                + "  reason TEXT NOT NULL"
                + ")");
            
            /* Creates the table that stores the state of warning list messages */
            statement.execute(
                  "CREATE TABLE IF NOT EXISTS MemberWarningsListMessage ("
                + "  message_id INTEGER NOT NULL PRIMARY KEY,"
                + "  warned_user_id INTEGER NOT NULL,"
                + "  page INTEGER NOT NULL"
                + ")");
        } catch (SQLException e) {
            System.err.println("Failed to initialise warnings DB");
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
     * Give a warning to a member and returns the created
     * {@code MemberWarning} or {@code null} if an error occurred
     * 
     * @param warnedMember The member receiving the warning
     * @param warnerMember The member giving the warning
     * @param reason       The reason for the warning
     * @returns The created member warning
     */
    public static final MemberWarning putWarning(Member warnedMember, Member warnerMember, String reason) {
        /* A transaction is being used as it requires two queries at the same
         * time: one to insert, and one to select the inserted row. A transaction
         * prevents a race condition
         */
        try (Connection transactionConnection = DriverManager.getConnection(dbPath)) {
            /* Starts the transaction */
            transactionConnection.setAutoCommit(false);

            PreparedStatement insertStatement = transactionConnection.prepareStatement(
                "INSERT INTO MemberWarning (received_at, warned_user_id, warner_user_id, reason) VALUES (?, ?, ?, ?)"
            );
            /* Using UTC+0 offset for timestamp */
            insertStatement.setLong(1, LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(0)));
            insertStatement.setLong(2, warnedMember.getIdLong());
            insertStatement.setLong(3, warnerMember.getIdLong());
            insertStatement.setString(4, reason);

            /* Insert */
            insertStatement.executeUpdate();

            Statement selectStatement = transactionConnection.createStatement();
            ResultSet set = selectStatement.executeQuery("SELECT * FROM MemberWarning WHERE warning_id == last_insert_rowid()");

            /* Check for result */
            if (!set.next()) {
                return new MemberWarning(-1, null, -1, -1, null);
            }

            /* Construct warning */
            MemberWarning warning = new MemberWarning(
                set.getInt("warning_id"),
                LocalDateTime.ofEpochSecond(set.getLong("received_at"), 0, ZoneOffset.ofHours(0)),
                set.getLong("warned_user_id"),
                set.getLong("warner_user_id"),
                set.getString("reason")
            );

            /* Commit the transaction */
            transactionConnection.commit();

            /* Return the warning */
            return warning;
        } catch (SQLException e) {
            System.out.println("Failed to put warning");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets a list of warnings that a member has, or {@code null} is
     * returned of something went wrong accessing the database
     * 
     * @param member The member
     * @return The list of warnings, or {@code null}
     */
    public static final List<MemberWarning> getMemberWarnings(Member member) {
        try (PreparedStatement statement = dbConnection.prepareStatement(
            "SELECT * FROM MemberWarning WHERE warned_user_id == ? ORDER BY received_at DESC"
        )) {
            statement.setLong(1, member.getIdLong());

            ArrayList<MemberWarning> warnings = new ArrayList<>();

            ResultSet set = statement.executeQuery();

            /* Iterate over the results and add to list */
            while (set.next()) {
                warnings.add(new MemberWarning(
                    set.getInt("warning_id"),
                    LocalDateTime.ofEpochSecond(set.getLong("received_at"), 0, ZoneOffset.ofHours(0)),
                    set.getLong("warned_user_id"),
                    set.getLong("warner_user_id"),
                    set.getString("reason")
                ));
            }

            /* Return results */
            return warnings;
        } catch (SQLException e) {
            System.err.println("Failed to get member warnings");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deletes all warnings for a member
     * 
     * @param member The member
     * @return If the operation was successful
     */
    public static final boolean clearMemberWarnings(Member member) {
        try (PreparedStatement statement = dbConnection.prepareStatement(
            "DELETE FROM MemberWarning WHERE warned_user_id == ?"
        )) {
            statement.setLong(1, member.getIdLong());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to clear member warnings");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Deletes a warning from a member
     * 
     * @param member The member the warning belongs to
     * @param id     The id of the warning
     * @return If the operation was successful
     */
    public static final boolean deleteWarning(Member member, int id) {
        try (PreparedStatement statement = dbConnection.prepareStatement(
            "DELETE FROM MemberWarning WHERE warned_user_id == ? AND warning_id == ?"
        )) {
            statement.setLong(1, member.getIdLong());
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete warning");
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    /**
     * Gets a specific member warning from its id and returns
     * a new {@code MemberWarning} or {@code null} if the id
     * does not exist
     * 
     * @param id The id of the warning
     * @return The warning, or {@code null}
     */
    public static final MemberWarning getMemberWarningById(int id) {
        try (PreparedStatement statement = dbConnection.prepareStatement(
            "SELECT * FROM MemberWarning WHERE warning_id == ?"
        )) {
            statement.setInt(1, id);

            ResultSet set = statement.executeQuery();

            /* Check result was found */
            if (!set.next()) {
                return null;
            }

            /* Return result */
            return new MemberWarning(
                set.getInt("warning_id"),
                LocalDateTime.ofEpochSecond(set.getLong("received_at"), 0, ZoneOffset.ofHours(0)),
                set.getLong("warned_user_id"),
                set.getLong("warner_user_id"),
                set.getString("reason")
            );
        } catch (SQLException e) {
            System.err.println("Failed to get warning by id");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Puts the data associated with a member warnings list message
     * into the database and returns if the operation was successful
     * 
     * @param message The warnings list message
     * @param member  The member the warnings belong to
     * @param page    The current page
     * @return If the operation was successful
     */
    public static final boolean putMemberWarningsListMessage(Message message, Member member, int page) {
        try (PreparedStatement statement = dbConnection.prepareStatement(
            "INSERT INTO MemberWarningsListMessage (message_id, warned_user_id, page) VALUES (?, ?, ?)"
        )) {
            statement.setLong(1, message.getIdLong());
            statement.setLong(2, member.getIdLong());
            statement.setInt(3, page);

            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("Failed to put member warnings list message");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the data associated with the warnings list message provided
     * or {@code null} if there is no match in the database or an error
     * occurs
     * 
     * @param message The message
     * @return The data, or {@code null}
     */
    public static final MemberWarningsListMessage getMemberWarningsListMessage(Message message) {
        try (PreparedStatement statement = dbConnection.prepareStatement(
            "SELECT * FROM MemberWarningsListMessage WHERE message_id == ?"
        )) {
            statement.setLong(1, message.getIdLong());

            ResultSet set = statement.executeQuery();

            /* Checks a result was found */
            if (!set.next()) {
                return null;
            }

            return new MemberWarningsListMessage(
                set.getLong("message_id"),
                set.getLong("warned_user_id"),
                set.getInt("page")
            );
        } catch (SQLException e) {
            System.err.println("Failed to get member warnings list message");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates the current page in the database for a member
     * warnings list message
     * 
     * @param message The message
     * @param page    The new page
     * @return If the operation was successful
     */
    public static final boolean updateMemberWarningsListMessagePage(Message message, int page) {
        try (PreparedStatement statement = dbConnection.prepareStatement(
            "UPDATE MemberWarningsListMessage SET page = ? WHERE message_id == ?"
        )) {
            statement.setInt(1, page);
            statement.setLong(2, message.getIdLong());

            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println("Failed to update member warnings list message page");
            e.printStackTrace();
            return false;
        }
    }
}
