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
 * A slash command to reset the level of a guild member
 * - restricted to {@code Permission.ADMINISTRATOR}
 */
public class ResetLevelCommand extends BotCommand {

	@Override
	public CommandData getCommandData() {
		return Commands.slash("reset-level", "Reset the level of a member")
            .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
            .addOption(OptionType.USER, "member", "The member to reset", true)
            .setGuildOnly(true);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        
        /* Get member and reset their level */
		Member member = event.getOption("member").getAsMember();
        DatabaseAccessorManager.getLevellingDatabaseAccessor().resetGuildMemberExperience(member);

        /* Send response message */
        event.getHook().sendMessageEmbeds(
            EmbedFactory.createSuccessEmbed("Member Level Reset", "Reset the level of the requested member to `0`")
        ).queue();
	}
}
