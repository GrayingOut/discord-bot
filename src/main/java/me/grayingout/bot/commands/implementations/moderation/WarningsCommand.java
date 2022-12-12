package me.grayingout.bot.commands.implementations.moderation;

import java.util.concurrent.TimeUnit;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.bot.interactables.warningslist.WarningsListMessage;
import me.grayingout.bot.interactables.warningslist.WarningsListMessageManager;
import me.grayingout.database.accessors.DatabaseAccessorManager;
import me.grayingout.database.entities.MemberWarning;
import me.grayingout.util.EmbedFactory;
import me.grayingout.util.SlashCommands;
import me.grayingout.util.Warnings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * A slash command for giving out, removing, listing, and
 * resetting warnings in a guild - restricted to
 * {@code Permission.MODERATE_MEMBERS} for the base command,
 * with additional restrictions for sub commands
 */
public final class WarningsCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("warnings", "Access the warning system")
            .addSubcommands(
                new SubcommandData("give", "Give a member a warning")
                    .addOption(OptionType.USER, "member", "The member to receive the warning", true)
                    .addOption(OptionType.STRING, "reason", "The reason for the warning", false),
                new SubcommandData("remove", "Remove a warning from a member")
                    .addOption(OptionType.USER, "member", "The member to receive the warning", true)
                    .addOption(OptionType.INTEGER, "id", "The warning id", true),
                new SubcommandData("clear", "Clear a member's warnings")
                    .addOption(OptionType.USER, "member", "The member whose warnings to reset", true),
                new SubcommandData("list", "Get a member's warnings")
                    .addOption(OptionType.USER, "member", "The member whose warnings to list", true)
            )
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS))
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        switch (event.getSubcommandName()) {
            case "give": {
                /* Get the member and moderator */
                Member member = event.getOption("member").getAsMember();
                Member moderator = event.getMember();
                
                /* Check if reason provided */
                String reason = "<no reason provided>";
                if (event.getOption("reason") != null) {
                    reason = event.getOption("reason").getAsString();
                }

                /* Warn the member */
                MemberWarning warning = DatabaseAccessorManager.getWarningsDatabaseAccessor().putWarning(
                    member,
                    moderator,
                    reason);
                
                /* Check warning was put into the database */
                if (warning != null) {
                    /* Send DM */
                    member.getUser().openPrivateChannel().complete().sendMessageEmbeds(Warnings.createDMWarningEmbed(member, moderator, reason)).queue();

                    /* Send success response */
                    event.getHook().sendMessageEmbeds(
                        Warnings.createWarningSuccessEmbed(member, moderator, reason, warning.getWarningId())
                    ).queue();
                    break;
                }
                
                /* Send error response */
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createWarningEmbed("Database Access Error", "Failed to warn member. Contact the the bot developer.")
                ).queue();
                break;
            }
            case "remove": {
                /* Check for additional permissions */
                if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    event.getHook().sendMessageEmbeds(
                        EmbedFactory.createErrorEmbed("Insufficient Permission", "This subcommand requires the `ADMINISTRATOR` permission.")
                    ).queue();
                    break;
                }

                Member member = event.getOption("member").getAsMember();

                Integer id = SlashCommands.safelyGetIntOption(event, "id");
                if (id == null) {
                    /* Invalid integer */
                    event.getHook().sendMessageEmbeds(EmbedFactory.createInvalidIntegerOptionEmbed("id")).queue();
                    break;
                }
                
                /* Check id is valid */
                if (id < 0) {
                    event.getHook().sendMessageEmbeds(
                        EmbedFactory.createWarningEmbed("Invalid Argument", "`id` must be greater than 0")
                    ).queue();
                    break;
                }

                /* Check warning exists */
                MemberWarning warning = DatabaseAccessorManager.getWarningsDatabaseAccessor().getMemberWarningById(member, id);
                if (warning == null) {
                    event.getHook().sendMessageEmbeds(
                        EmbedFactory.createWarningEmbed("Invalid Id", "Warning not found for the provided id")
                    ).queue();
                    break;
                }
                
                /* Send response message */
                DatabaseAccessorManager.getWarningsDatabaseAccessor().deleteWarning(member, id);
                
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createSuccessEmbed("Warning Removed", "Warning has been removed from member")
                ).queue();
                break;
            }
            case "clear": {
                /* Check for additional permission */
                if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    event.getHook().sendMessageEmbeds(
                        EmbedFactory.createErrorEmbed("Insufficient Permission", "This subcommand requires the `ADMINISTRATOR` permission.")
                    ).queue();
                    break;
                }

                /* Clear the members warnings */
                Member member = event.getOption("member").getAsMember();
                DatabaseAccessorManager.getWarningsDatabaseAccessor().clearMemberWarnings(member);

                /* Send success response */
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createSuccessEmbed("Cleared Warnings", "All warnings have been cleared for " + member.getAsMention())
                ).queue();
                break;
            }
            case "list": {
                /* Get the member who's warnings to show */
                Member member = event.getOption("member").getAsMember();

                /* Member no longer in guild */
                if (member == null) {
                    event.getHook().sendMessageEmbeds(
                        Warnings.createMemberNotFoundEmbed()
                    ).queue(message -> {
                        message.delete().queueAfter(3, TimeUnit.SECONDS);
                    });
                    return;
                }

                /* Create the message data */
                MessageCreateData data = WarningsListMessage.createWarningsListMessageData(member);
                event.getHook().sendMessage(data).queue(m -> {
                    /* Register message */
                    WarningsListMessageManager.register(new WarningsListMessage(m, member));
                });
                break;
            }
            default:
                throw new RuntimeException("Unhandled /warnings subcommand: " + event.getSubcommandName());
        }
    }
}
