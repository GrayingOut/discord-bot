package me.grayingout.bot.commands.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.accessors.DatabaseAccessorManager;
import me.grayingout.database.entities.GuildLevelRole;
import me.grayingout.util.EmbedFactory;
import me.grayingout.util.SlashCommands;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A slash command for setting, removing, and listing
 * level role rewards - restricted to {@code Permission.MANAGE_ROLES}
 * and {@code Permission.MANAGE_SERVER}
 */
public final class LevelRolesCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("level-roles", "Configure level role rewards")
            .addSubcommands(
                new SubcommandData("add", "Add a new level role reward")
                    .addOption(OptionType.ROLE, "role", "The role to reward", true)
                    .addOption(OptionType.INTEGER, "level", "The level to reward it at", true),
                new SubcommandData("remove", "Remove a level role reward")
                    .addOption(OptionType.ROLE, "role", "The role to remove", true),
                new SubcommandData("list", "Get a list of level role rewards")
            )
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES, Permission.MANAGE_SERVER))
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        switch (event.getSubcommandName()) {
            case "add": {
                /* Get the level */
                Integer level = SlashCommands.safelyGetIntOption(event, "level");
                if (level == null) {
                    event.getHook().sendMessageEmbeds(EmbedFactory.createInvalidIntegerOptionEmbed("level")).queue();
                    break;
                }

                /* Check level is valid */
                if (level < 1) {
                    event.getHook().sendMessageEmbeds(
                        EmbedFactory.createWarningEmbed("Invalid Argument", "`level` must be greater than 0")
                    ).queue();
                    break;
                }

                /* Get the role */
                Role role = event.getOption("role").getAsRole();

                /* Cannot add role higher than self */
                if (role.getPosition() > event.getGuild().getBotRole().getPosition()) {
                    event.getHook().sendMessageEmbeds(
                        EmbedFactory.createErrorEmbed("Insufficient Permission", role.getAsMention() + " is higher in the role hierarchy than me")
                    ).queue();
                    break;
                }

                /* Cannot add @everyone role */
                if (role.getIdLong() == role.getGuild().getIdLong()) {
                    event.getHook().sendMessageEmbeds(
                        EmbedFactory.createWarningEmbed("Invalid Argument", "`role` cannot be " + role.getAsMention())
                    ).queue();
                    break;
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
                break;
            }
            case "remove": {
                /* Get the role ro remove */
                Role role = event.getOption("role").getAsRole();

                /* Delete the role */
                DatabaseAccessorManager.getLevellingDatabaseAccessor().deleteGuildLevelRole(role.getIdLong());

                /* Send response message */
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createSuccessEmbed("Removed Level Role", role.getAsMention() + " has been removed from any levels")
                ).queue();
                break;
            }
            case "list": {
                /* Get the roles */
                List<GuildLevelRole> levelRoles = DatabaseAccessorManager
                    .getLevellingDatabaseAccessor()
                    .getGuildLevelRoles(event.getGuild());
            
                /* Store the fields */
                List<Field> fields = new ArrayList<>();

                /* Sort the roles in order of required level in descending order */
                levelRoles = levelRoles.stream()
                    .sorted(new GuildLevelRole.GuildLevelRoleComparator(false))
                    .collect(Collectors.toList());
                
                int previousRequiredLevel = -1;

                /* This basically combines roles with same required level together */
                for (GuildLevelRole levelRole : levelRoles) {
                    if (levelRole.getRequiredLevel() != previousRequiredLevel) {
                        previousRequiredLevel = levelRole.getRequiredLevel();
                        fields.add(new Field(
                            "Level: " + previousRequiredLevel,
                            levelRole.getRole().getAsMention(),
                            false
                        ));
                        continue;
                    }

                    Field oldField = fields.get(fields.size()-1);

                    fields.set(fields.size()-1, new Field(
                        oldField.getName(),
                        oldField.getValue() + "\n" + levelRole.getRole().getAsMention(),
                        oldField.isInline()
                    ));
                }

                /* Create response embed */
                MessageEmbed embed = EmbedFactory.createGenericEmbed(
                    "🎭 Level Role Rewards",
                    "The following roles are available at the following levels",
                    fields.toArray(new Field[] {}));
                
                /* Send response */
                event.getHook().sendMessageEmbeds(embed).queue();
                break;
            }
        }
    }   
}
