package me.grayingout.database.objects;

import net.dv8tion.jda.api.entities.Role;

/**
 * A class that contains the data and helper methods
 * for a level reward role
 */
public final class GuildLevelRole {
    
    /**
     * The role associated with the level role
     */
    private final Role role;

    /**
     * The level required for the role
     */
    private final int level;

    /**
     * Creates a new {@code GuildLevelRole}
     */
    public GuildLevelRole(Role role, int level) {
        this.role = role;
        this.level = level;
    }

    /**
     * Gets the role for this level role
     * 
     * @return The role
     */
    public final Role getRole() {
        return this.role;
    }

    /**
     * Gets the level required for this role
     * 
     * @return The level required
     */
    public final int getRequiredLevel() {
        return this.level;
    }
}
