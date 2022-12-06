package me.grayingout.database.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import me.grayingout.database.objects.GuildMemberLevelExperience;
import me.grayingout.database.query.DatabaseQuery;
import me.grayingout.util.Levelling;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

/**
 * Class for accessing the the levelling data from
 * the levelling database
 */
public final class LevellingDatabaseAccessor extends DatabaseAccessor {

    public LevellingDatabaseAccessor() {
        super("levelling.db");
    }

    @Override
    void init() {
        queueQuery(new DatabaseQuery<Void>() {
            @Override
            public Void execute(Connection connection) throws SQLException {
                Statement statement = connection.createStatement();

                /* Creates the table that stores member levelling data */
                statement.execute(
                    "CREATE TABLE IF NOT EXISTS GuildMemberLevelExperience ("
                  + "  guild_id INTEGER NOT NULL,"
                  + "  user_id INTEGER NOT NULL,"
                  + "  level_experience INTEGER NOT NULL DEFAULT 0"
                  + ")");

                return null;
            }  
        });
    }

    /**
     * Gets the top 5 member experience levels in a guild
     * 
     * @return The top members
     */
    @SuppressWarnings("unchecked")
    public final List<GuildMemberLevelExperience> getTopGuildMembers(Guild guild) {
        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<List<GuildMemberLevelExperience>>() {
            @Override
            public List<GuildMemberLevelExperience> execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT level_experience, user_id FROM GuildMemberLevelExperience WHERE guild_id == ? AND level_experience > 0 ORDER BY level_experience DESC LIMIT 5"
                );

                statement.setLong(1, guild.getIdLong());

                ResultSet set = statement.executeQuery();

                List<GuildMemberLevelExperience> guildMemberLevelExperiences = new ArrayList<>();

                /* Add all guild member experiences */
                while (set.next()) {
                    guildMemberLevelExperiences.add(
                        new GuildMemberLevelExperience(
                            guild.retrieveMemberById(set.getLong("user_id")).complete(),
                            set.getInt("level_experience")
                        )
                    );
                }
                
                return guildMemberLevelExperiences;
            }
        });

        try {
            return (List<GuildMemberLevelExperience>) future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Arrays.asList();
        }
    }

    /**
     * Sets the level experience for a guild member 
     * 
     * @param member     The member to set the level experience of
     * @param experience The experience to set it to
     */
    public final void setGuildMemberLevelExperience(Member member, int experience) {
        queueQuery(new DatabaseQuery<Void>() {
			@Override
			public Void execute(Connection connection) throws SQLException {
                /* Check to see if they have an entry in the database */
                PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT * FROM GuildMemberLevelExperience WHERE guild_id == ? AND user_id == ?"
                );

                selectStatement.setLong(1, member.getGuild().getIdLong());
                selectStatement.setLong(2, member.getIdLong());

                ResultSet set = selectStatement.executeQuery();

                /* They have no entry, so insert */
                if (!set.next()) {
                    /* Insert new row */
                    PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO GuildMemberLevelExperience (guild_id, user_id, level_experience) VALUES (?, ?, ?)"
                    );
    
                    insertStatement.setLong(1, member.getGuild().getIdLong());
                    insertStatement.setLong(2, member.getIdLong());
                    insertStatement.setInt(3, experience);
    
                    insertStatement.execute();

                    return null;
                }

                /* Set the member's experience to 'experience' */
                PreparedStatement updateStatement = connection.prepareStatement(
                    "UPDATE GuildMemberLevelExperience SET level_experience = ? WHERE guild_id == ? AND user_id == ?"
                );

                updateStatement.setInt(1, experience);
                updateStatement.setLong(2, member.getGuild().getIdLong());
                updateStatement.setLong(3, member.getIdLong());

                updateStatement.execute();

				return null;
			}
        });
    }

    /**
     * Sets the level experience of a guild member to 0
     * 
     * @param member The member to reset
     */
    public final void resetGuildMemberExperience(Member member) {
        setGuildMemberLevelExperience(member, 0);
    }

    /**
     * Adds a provided amount of experience to a guild members level experience
     * and returns the new level, if they have a new level, else
     * {@code -1} is returned
     * 
     * @param member The guild member
     * @param experience Amount to add
     * @return The new level, or {@code -1}
     * @throws IllegalArgumentException If {@code experience} is less than 0
     */
    public final int addExperienceToGuildMember(Member member, int experience) {
        if (experience < 0) {
            throw new IllegalArgumentException("Cannot add negative experience");
        }

        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<Integer>() {
			@Override
			public Integer execute(Connection connection) throws SQLException {
                /* Check to see if they have an entry in the database */
                PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT * FROM GuildMemberLevelExperience WHERE guild_id == ? AND user_id == ?"
                );

                selectStatement.setLong(1, member.getGuild().getIdLong());
                selectStatement.setLong(2, member.getIdLong());

                ResultSet set = selectStatement.executeQuery();

                /* User has no entry in the database */
                if (!set.next()) {
                    /* Insert 'experience' into the database */
                    PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO GuildMemberLevelExperience (guild_id, user_id, level_experience) VALUES (?, ?, ?)"
                    );

                    insertStatement.setLong(1, member.getGuild().getIdLong());
                    insertStatement.setLong(2, member.getIdLong());
                    insertStatement.setInt(3, experience);

                    insertStatement.execute();

                    /* Check if user has a new level */
                    if (Levelling.getLevelFromExperience(experience) != 0) {
                        return Levelling.getLevelFromExperience(experience);
                    }
                    return -1;
                }

                int currentExperience = set.getInt("level_experience");

                /* Add 'experience' to current experience */
                PreparedStatement updateStatement = connection.prepareStatement(
                    "UPDATE GuildMemberLevelExperience SET level_experience = ? WHERE guild_id == ? AND user_id == ?"
                );

                updateStatement.setInt(1, set.getInt("level_experience") + experience);
                updateStatement.setLong(2, member.getGuild().getIdLong());
                updateStatement.setLong(3, member.getIdLong());

                updateStatement.execute();

                /* Check if user has a new level */
                if (Levelling.getLevelFromExperience(currentExperience) != Levelling.getLevelFromExperience(currentExperience + experience)) {
                    return Levelling.getLevelFromExperience(currentExperience + experience);
                }
				return -1;
			} 
        });

        try {
            return (int) future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Gets the level experience of a guild member
     * 
     * @param member The guild member
     * @return Their level experience
     */
    public final GuildMemberLevelExperience getGildMemberLevelExperience(Member member) {
        CompletableFuture<Object> future = queueQuery(new DatabaseQuery<Integer>() {
            @Override
            public Integer execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT level_experience FROM GuildMemberLevelExperience WHERE guild_id == ? AND user_id == ?"
                );

                statement.setLong(1, member.getGuild().getIdLong());
                statement.setLong(2, member.getIdLong());

                ResultSet set = statement.executeQuery();

                if (!set.next()) {
                    return 0;
                }

                return set.getInt("level_experience");
            }
        });

        try {
            return new GuildMemberLevelExperience(member, (int) future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new GuildMemberLevelExperience(member, -1);
        }
    }
}
