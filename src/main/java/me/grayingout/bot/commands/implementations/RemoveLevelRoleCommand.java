package me.grayingout.bot.commands.implementations;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.accessor.DatabaseAccessorManager;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command to remove a level role - restricted
 * to {@code Permission.MANAGE_ROLES}
 */
public final class RemoveLevelRoleCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("remove-level-role", "Remove a level role reward")
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES))
            .addOption(OptionType.ROLE, "role", "The role to remove", true)
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Role role = event.getOption("role").getAsRole();

        /* Delete the role */
        DatabaseAccessorManager.getLevellingDatabaseAccessor().deleteGuildLevelRole(role.getIdLong());

        /* Send response message */
        event.getHook().sendMessageEmbeds(
            EmbedFactory.createSuccessEmbed("Removed Level Role", role.getAsMention() + " has been removed from any levels")
        ).queue();
    }
    
}
