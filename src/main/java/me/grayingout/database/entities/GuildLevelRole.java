package me.grayingout.database.entities;

import java.util.Comparator;

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

    /**
     * A comparator class for {@code GuildLevelRole}s
     */
    public static final class GuildLevelRoleComparator implements Comparator<GuildLevelRole> {

        /**
         * If it should sort in ascending order
         */
        private final boolean ascendingOrder;

        /**
         * Creates a new {@code GuildLevelRoleComparator} that either sorts
         * in ascending order, if {@code ascendingOrder} is true, else descending
         * order
         * 
         * @param ascendingOrder If it should sort in ascending order
         */
        public GuildLevelRoleComparator(boolean ascendingOrder) {
            this.ascendingOrder = ascendingOrder;
        }

        @Override
        public int compare(GuildLevelRole o1, GuildLevelRole o2) {
            if (o1.getRequiredLevel() < o2.getRequiredLevel()) {
                return ascendingOrder ? -1 : 1;
            }

            if (o1.getRequiredLevel() > o2.getRequiredLevel()) {
                return ascendingOrder ? 1 : -1;
            }

            return 0;
        }
    }
}
