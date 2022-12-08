package me.grayingout.database.entities;

import me.grayingout.util.Levelling;
import net.dv8tion.jda.api.entities.Member;

/**
 * Holds the data and helper methods for data from
 * the levelling database
 */
public final class GuildMemberLevelExperience {

    /**
     * The member the experience belongs to
     */
    private final Member member;

    /**
     * The experience of the member
     */
    private final int experience;

    /**
     * The experience needed for the current level
     */
    private final int currentLevelExperience;

    /**
     * The current level of the member
     */
    private final int level;

    /**
     * The experience needed to get to the next level
     */
    private final int nextLevelExperience;

    /**
     * The current progress, as percentage, to the next
     * level
     */
    private final double progressToNextLevel;
    
    /**
     * Creates a new {@code GuildMemberLevelExperience}
     * 
     * @param experience The experience of the member
     */
    public GuildMemberLevelExperience(Member member, int experience) {
        this.member = member;
        this.experience = experience;
        this.level = Levelling.getLevelFromExperience(experience);
        this.currentLevelExperience = Levelling.getExperienceForLevel(this.level);
        this.nextLevelExperience = Levelling.getExperienceForLevel(this.level + 1);
        this.progressToNextLevel = this.experience/(this.nextLevelExperience * 1.0);
    }

    /**
     * Gets the member this experience object
     * belongs to
     * 
     * @return The member
     */
    public final Member getMember() {
        return this.member;
    }

    /**
     * Gets the current experience of the member
     * 
     * @return The current experience
     */
    public final int getExperience() {
        return this.experience;
    }

    /**
     * Gets the current level of the member
     * 
     * @return The current level
     */
    public final int getLevel() {
        return this.level;
    }

    /**
     * Gets the experience needed for the current
     * level
     * 
     * @return The experience needed
     */
    public final int getCurrentLevelExperience() {
        return this.currentLevelExperience;
    }

    /**
     * Gets the experience needed to get to the
     * next level
     * 
     * @return The next level experience
     */
    public final int getNextLevelExperience() {
        return this.nextLevelExperience;
    }

    /**
     * Gets the members progress, as a percentage, to
     * the next level
     * 
     * @return The progress to the next level
     */
    public final double getProgressToNextLevel() {
        return this.progressToNextLevel;
    }
}
