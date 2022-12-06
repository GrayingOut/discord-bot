package me.grayingout.util;

/**
 * utility methods for the levelling system
 */
public final class Levelling {
    
    /**
     * Get the experience required for a specific level using the
     * model {@code 5x^2}
     * 
     * @param level The level
     * @return The experience required
     */
    public static final int getExperienceForLevel(int level) {
        return 5 * level * level;
    }

    /**
     * Get the level from level experience using the reverse model to
     * the model {@code 5x^2}
     * 
     * @param experience The experience amount
     * @return The level
     */
    public static final int getLevelFromExperience(int experience) {
        if (experience < 0) {
            return -1;
        }
        
        return (int) Math.floor(Math.sqrt(experience/5));
    }
}
