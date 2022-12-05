package me.grayingout.bot.commands.implementations;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.accessor.DatabaseAccessorManager;
import me.grayingout.database.objects.MemberWarning;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command to remove a specific warning
 * from a member - restricted to {@code Permission.ADMINISTRATOR}
 */
public class RemoveWarningCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands
            .slash("remove-warning", "Remove a warning from a member using the warning id")
            .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
            .addOption(OptionType.USER, "member", "The member to remove the warning from", true)
            .addOption(OptionType.INTEGER, "id", "The id of the warning", true)
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Member member = event.getOption("member").getAsMember();

        int id = -1;
        try {
            id = event.getOption("id").getAsInt();
        } catch (ArithmeticException e) {
            /* Invalid integer */
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createExceptionEmbed(e, "The provided `id` cannot be processed by the application")
            ).queue();
            return;
        }
        
        /* Check id is valid */
        if (id < 0) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Invalid Argument", "`id` must be greater than 0")
            ).queue();
            return;
        }

        /* Check warning exists */
        MemberWarning warning = DatabaseAccessorManager.getWarningsDatabaseAccessor().getMemberWarningById(member, id);
        
        if (warning == null) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Invalid Id", "Warning not found for the provided id")
            ).queue();
            return;
        }
        
        /* Send response message */
        DatabaseAccessorManager.getWarningsDatabaseAccessor().deleteWarning(member, id);
        
        event.getHook().sendMessageEmbeds(
            EmbedFactory.createSuccessEmbed("Warning Removed", "Warning has been removed from member")
        ).queue();
    }
}
