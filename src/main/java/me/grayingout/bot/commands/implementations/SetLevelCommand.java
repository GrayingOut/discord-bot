package me.grayingout.bot.commands.implementations;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.accessor.DatabaseAccessorManager;
import me.grayingout.util.EmbedFactory;
import me.grayingout.util.Levelling;
import me.grayingout.util.SlashCommands;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command used to set the level of a guild
 * member - restricted to {@code Permission.ADMINISTRATOR}
 */
public class SetLevelCommand extends BotCommand {

	@Override
	public CommandData getCommandData() {
		return Commands.slash("set-level", "Set the level of a member")
            .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
            .addOption(OptionType.USER, "member", "The member to set the level of", true)
            .addOption(OptionType.INTEGER, "level", "The level to set it to", true)
            .setGuildOnly(true);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply().queue();

        Integer level = SlashCommands.safelyGetIntOption(event, "level");
        if (level == null) {
            /* Invalid integer */
            event.getHook().sendMessageEmbeds(EmbedFactory.createInvalidIntegerOptionEmbed("level")).queue();
            return;
        }

        /* Check valid level */
        if (level < 0) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Invalid Argument", "`level` must be greater than 0")
            ).queue();
            return;
        }

        Member member = event.getOption("member").getAsMember();

        DatabaseAccessorManager.getLevellingDatabaseAccessor()
            .setGuildMemberLevelExperience(member, Levelling.getExperienceForLevel(level));

        event.getHook().sendMessageEmbeds(
            EmbedFactory.createSuccessEmbed("Updated Members Level", "The members level has been set to `" + level + "`")
        ).queue();
	}
}
