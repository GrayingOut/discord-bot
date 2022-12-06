package me.grayingout.util;

import java.util.List;

import me.grayingout.database.accessor.DatabaseAccessorManager;
import me.grayingout.database.objects.GuildLevelRole;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

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

    /**
     * Update the level roles of a member
     * 
     * @param member The member to update
     */
    public static final void updateMemberLevelRoles(Member member) {
        /* Get member level */
        int level = DatabaseAccessorManager
            .getLevellingDatabaseAccessor()
            .getGildMemberLevelExperience(member)
            .getLevel();

        /* Get the level roles */
        List<GuildLevelRole> levelRoles = DatabaseAccessorManager
            .getLevellingDatabaseAccessor()
            .getGuildLevelRoles(member.getGuild());
        
        /* Get the member's current roles */
        List<Role> roles = member.getRoles();
        
        /* Check the level roles against the member's roles */
        for (GuildLevelRole levelRole : levelRoles) {
            /* Member missing role */
            if (levelRole.getRequiredLevel() <= level && !roles.contains(levelRole.getRole())) {
                member.getGuild().addRoleToMember(member, levelRole.getRole()).queue();
                continue;
            }

            /* Member shouldn't have role */
            if (levelRole.getRequiredLevel() > level && roles.contains(levelRole.getRole())) {
                member.getGuild().removeRoleFromMember(member, levelRole.getRole()).queue();
                continue;
            }
        }
    }
}
