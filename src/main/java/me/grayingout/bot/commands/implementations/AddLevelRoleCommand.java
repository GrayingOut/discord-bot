package me.grayingout.bot.commands.implementations;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.accessor.DatabaseAccessorManager;
import me.grayingout.util.EmbedFactory;
import me.grayingout.util.SlashCommands;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command used to add a new level role
 * reward - restricted to {@code Permission.MANAGE_ROLES}
 */
public final class AddLevelRoleCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("add-level-role", "Adds a new role reward for a specific level")
            .addOption(OptionType.ROLE, "role", "The role to use", true)
            .addOption(OptionType.INTEGER, "level", "The level required", true)
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES))
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        
        /* Get the level */
        Integer level = SlashCommands.safelyGetIntOption(event, "level");
        if (level == null) {
            event.getHook().sendMessageEmbeds(EmbedFactory.createInvalidIntegerOptionEmbed("level")).queue();
            return;
        }

        /* Check level is valid */
        if (level < 1) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Invalid Argument", "`level` must be greater than 1")
            ).queue();
            return;
        }

        Role role = event.getOption("role").getAsRole();

        /* Cannot add @everyone role */
        if (role.getIdLong() == role.getGuild().getIdLong()) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Invalid Argument", "`role` cannot be " + role.getAsMention())
            ).queue();
            return;
        }

        /* Update the level */
        DatabaseAccessorManager.getLevellingDatabaseAccessor().addGuildLevelRole(role, level);
        
        /* Send success */
        event.getHook().sendMessageEmbeds(
            EmbedFactory.createSuccessEmbed(
                "Added Level Role",
                String.format("%s has been added as a role reward for level %s", role.getAsMention(), level)
            )
        ).queue();
    }
}
