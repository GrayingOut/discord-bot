package me.grayingout.bot.commands.implementations.audio;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.accessors.DatabaseAccessorManager;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A slash command to set, remove, and get the DJ role - restricted
 * to {@code Permission.MANAGE_SERVER} and {@code Permission.MANAGE_ROLES}
 */
public final class DJRoleCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("dj-role", "Set the bot DJ role")
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES, Permission.MANAGE_SERVER))
            .addSubcommands(
                new SubcommandData("set", "Set the DJ role")
                    .addOption(OptionType.ROLE, "role", "The role to use", true),
                new SubcommandData("remove", "Remove the DJ role"),
                new SubcommandData("get", "Get the DJ role")
            );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        switch (event.getSubcommandName()) {
            case "set": {
                Role role = event.getOption("role").getAsRole();

                DatabaseAccessorManager.getConfigurationDatabaseAccessor()
                    .updateGuildDJRole(event.getGuild(), role);
                
                /* Send response */
                event.getHook().sendMessageEmbeds(EmbedFactory.createSuccessEmbed(
                    "DJ Role Updated",
                    "The DJ role has been set to " + role.getAsMention()
                )).queue();
                break;
            }
            case "remove": {
                DatabaseAccessorManager.getConfigurationDatabaseAccessor()
                    .removeGuildDJRole(event.getGuild());
            
                /* Send response */
                event.getHook().sendMessageEmbeds(EmbedFactory.createSuccessEmbed(
                    "DJ Role Removed",
                    "The DJ role has been removed"
                )).queue();
                break;
            }
            case "get": {
                Role role = DatabaseAccessorManager.getConfigurationDatabaseAccessor()
                    .getGuildDJRole(event.getGuild());
            
                /* Send response */
                event.getHook().sendMessageEmbeds(EmbedFactory.createSuccessEmbed(
                    "DJ Role",
                    role == null ? "There is currently no DJ role" : "The DJ role currently is " + role.getAsMention()
                )).queue();
                break;
            }
        }
    }
    
}
