package me.grayingout.database.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import me.grayingout.database.objects.MemberWarning;
import me.grayingout.database.objects.MemberWarningsListMessage;
import me.grayingout.database.query.DatabaseQuery;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

/**
 * Class for accessing and updating information
 * in the warnings sqlite database
 */
public final class WarningsDatabaseAccessor extends DatabaseAccessor {

    /**
     * Creates a new {@code WarningsDatabaseAccessor}
     */
    public WarningsDatabaseAccessor() {
        super("warnings.db");
    }

    @Override
    public final void init() {
        queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                Statement statement = connection.createStatement();

                /* Creates the table that stores member warnings */
                statement.execute(
                    "CREATE TABLE IF NOT EXISTS MemberWarning ("
                  + "  warning_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                  + "  guild_id INTEGER NOT NULL,"
                  + "  member_id INTEGER NOT NULL,"
                  + "  moderator_id INTEGER NOT NULL,"
                  + "  received_at INTEGER NOT NULL,"
                  + "  reason TEXT NOT NULL"
                  + ")");
            
                /* Creates the table that stores the state of warning list messages */
                statement.execute(
                    "CREATE TABLE IF NOT EXISTS MemberWarningsListMessage ("
                  + "  message_id INTEGER NOT NULL PRIMARY KEY,"
                  + "  channel_id INTEGER NOT NULL,"
                  + "  guild_id INTEGER NOT NULL,"
                  + "  member_id INTEGER NOT NULL,"
                  + "  page INTEGER NOT NULL"
                  + ")");
                
                return null;
            }
        });
    }

    /**
     * Give a warning to a member and returns the created
     * {@code MemberWarning} or {@code null} if an error occurred
     * 
     * @param member    The member receiving the warning
     * @param moderator The member giving the warning
     * @param reason    The reason for the warning
     * @returns The created member warning
     */
    public final MemberWarning putWarning(Member member, Member moderator, String reason) {
        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<MemberWarning>() {
            @Override
            public MemberWarning execute(Connection connection) throws SQLException {
                PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO MemberWarning (guild_id, member_id, moderator_id, received_at, reason) VALUES (?, ?, ?, ?, ?)"
                );
                /* Using UTC+0 offset for timestamp */
                insertStatement.setLong(1, member.getGuild().getIdLong());
                insertStatement.setLong(2, member.getIdLong());
                insertStatement.setLong(3, moderator.getIdLong());
                insertStatement.setLong(4, LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(0)));
                insertStatement.setString(5, reason);

                /* Insert */
                insertStatement.executeUpdate();

                Statement selectStatement = connection.createStatement();
                ResultSet set = selectStatement.executeQuery("SELECT * FROM MemberWarning WHERE warning_id == last_insert_rowid()");

                /* Check for result */
                if (!set.next()) {
                    return null;
                }

                /* Construct warning */
                MemberWarning warning = new MemberWarning(
                    set.getInt("warning_id"),
                    set.getLong("guild_id"),
                    set.getLong("member_id"),
                    set.getLong("moderator_id"),
                    LocalDateTime.ofEpochSecond(set.getLong("received_at"), 0, ZoneOffset.ofHours(0)),
                    set.getString("reason")
                );

                return warning;
            }
            
        });

        try {
            return (MemberWarning) future.get();
        } catch (InterruptedException | ExecutionException e) {
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
    @SuppressWarnings("unchecked")
    public final List<MemberWarning> getMemberWarnings(Member member) {
        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<ArrayList<MemberWarning>>() {
            @Override
            public ArrayList<MemberWarning> execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM MemberWarning WHERE guild_id == ? AND member_id == ? ORDER BY received_at DESC"
                );

                statement.setLong(1, member.getGuild().getIdLong());
                statement.setLong(2, member.getIdLong());
    
                ArrayList<MemberWarning> warnings = new ArrayList<>();
    
                ResultSet set = statement.executeQuery();
    
                /* Iterate over the results and add to list */
                while (set.next()) {
                    warnings.add(new MemberWarning(
                        set.getInt("warning_id"),
                        set.getLong("guild_id"),
                        set.getLong("member_id"),
                        set.getLong("moderator_id"),
                        LocalDateTime.ofEpochSecond(set.getLong("received_at"), 0, ZoneOffset.ofHours(0)),
                        set.getString("reason")
                    ));
                }
    
                /* Return results */
                return warnings;
            }
        });

        try {
            return (ArrayList<MemberWarning>) future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deletes all warnings for a member
     * 
     * @param member The member
     */
    public final void clearMemberWarnings(Member member) {
        queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM MemberWarning WHERE guild_id == ? AND member_id == ?"
                );

                statement.setLong(1, member.getGuild().getIdLong());
                statement.setLong(2, member.getIdLong());
    
                statement.executeUpdate();

                return null;
            }
        });
    }

    /**
     * Deletes a warning from a member
     * 
     * @param member The member the warning belongs to
     * @param id     The id of the warning
     */
    public final void deleteWarning(Member member, int id) {
        queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM MemberWarning WHERE warning_id == ? AND guild_id == ? AND member_id == ?"
                );

                statement.setInt(1, id);
                statement.setLong(2, member.getGuild().getIdLong());
                statement.setLong(3, member.getIdLong());
    
                statement.executeUpdate();

                return null;
            }
        });
    }

    /**
     * Gets a specific member warning from its id and returns
     * a new {@code MemberWarning} or {@code null} if the id
     * does not exist
     * 
     * @param member The member the warning belongs to
     * @param id     The id of the warning
     * @return The warning, or {@code null}
     */
    public final MemberWarning getMemberWarningById(Member member, int id) {
        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<MemberWarning>() {
            @Override
            public MemberWarning execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM MemberWarning WHERE warning_id == ? AND member_id == ? AND guild_id == ?"
                );

                statement.setInt(1, id);
                statement.setLong(2, member.getIdLong());
                statement.setLong(3, member.getGuild().getIdLong());

                ResultSet set = statement.executeQuery();

                /* Check result was found */
                if (!set.next()) {
                    return null;
                }

                /* Return result */
                return new MemberWarning(
                    set.getInt("warning_id"),
                    set.getLong("guild_id"),
                    set.getLong("member_id"),
                    set.getLong("moderator_id"),
                    LocalDateTime.ofEpochSecond(set.getLong("received_at"), 0, ZoneOffset.ofHours(0)),
                    set.getString("reason")
                );
            }
        });

        try {
            return (MemberWarning) future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Puts the data associated with a member warnings list message
     * into the database
     * 
     * @param message The warnings list message
     * @param member  The member the warnings belong to
     * @param page    The current page
     */
    public final void putMemberWarningsListMessage(Message message, Member member, int page) {
        queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO MemberWarningsListMessage (message_id, channel_id, guild_id, member_id, page) VALUES (?, ?, ?, ?, ?)"
                );

                statement.setLong(1, message.getIdLong());
                statement.setLong(2, message.getChannel().getIdLong());
                statement.setLong(3, message.getGuild().getIdLong());
                statement.setLong(4, member.getIdLong());
                statement.setInt(5, page);
    
                statement.executeUpdate();

                return null;
            }
        });
    }

    /**
     * Gets the data associated with the warnings list message provided
     * or {@code null} if there is no match in the database or an error
     * occurs
     * 
     * @param message The message
     * @return The data, or {@code null}
     */
    public final MemberWarningsListMessage getMemberWarningsListMessage(Message message) {
        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<MemberWarningsListMessage>() {
            @Override
            public MemberWarningsListMessage execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM MemberWarningsListMessage WHERE message_id == ?"
                );
                
                statement.setLong(1, message.getIdLong());

                ResultSet set = statement.executeQuery();

                /* Check result was found */
                if (!set.next()) {
                    return null;
                }

                /* Construct MemberWarningsListMessage */
                return new MemberWarningsListMessage(
                    set.getLong("message_id"),
                    set.getLong("channel_id"),
                    set.getLong("guild_id"),
                    set.getLong("member_id"),
                    set.getInt("page")
                );
            }
        });

        try {
            return (MemberWarningsListMessage) future.get();
        } catch (InterruptedException | ExecutionException e) {
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
     */
    public final void updateMemberWarningsListMessagePage(Message message, int page) {
        queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "UPDATE MemberWarningsListMessage SET page = ? WHERE message_id == ?"
                );

                statement.setInt(1, page);
                statement.setLong(2, message.getIdLong());
    
                statement.executeUpdate();

                return null;
            }
        });
    }
}
