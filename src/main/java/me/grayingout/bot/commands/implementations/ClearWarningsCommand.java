package me.grayingout.bot.commands.implementations;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.accessor.DatabaseAccessorManager;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command to delete all of a guild members
 * warnings - restricted to {@code Permission.ADMINISTRATOR}
 */
public class ClearWarningsCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands
            .slash("clear-warnings", "Clears all warnings of a member")
            .addOption(OptionType.USER, "member", "The member to clear warnings from", true)
            .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Member member = event.getOption("member").getAsMember();

        DatabaseAccessorManager.getWarningsDatabaseAccessor().clearMemberWarnings(member);

        event.getHook().sendMessageEmbeds(
            EmbedFactory.createSuccessEmbed("Cleared Warnings", "All warnings have been cleared for " + member.getAsMention())
        ).queue();
    }
}
