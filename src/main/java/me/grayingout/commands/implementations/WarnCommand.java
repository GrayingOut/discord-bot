package me.grayingout.commands.implementations;

import me.grayingout.commands.BotCommand;
import me.grayingout.database.warnings.MemberWarning;
import me.grayingout.database.warnings.WarningsDatabase;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command to warn a guild member
 * - restricted to {@code Permission.MODERATE_MEMBERS}
 */
public class WarnCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands
            .slash("warn", "Warn a member")
            .addOption(OptionType.USER, "member", "The member to warn", true)
            .addOption(OptionType.STRING, "reason", "The reason for the warning", false)
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS))
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        
        /* Get the warned member and warner member */
        Member warnedMember = event.getOption("member").getAsMember();
        Member warnerMember = event.getMember();
        
        /* Default reason */
        String reason = "<no reason provided>";
        
        /* Check if reason provided */
        if (event.getOption("reason") != null) {
            reason = event.getOption("reason").getAsString();
        }

        /* Put the warning into the database */
        MemberWarning warning = WarningsDatabase.putWarning(
            warnedMember,
            warnerMember,
            reason);
        
        /* Send response */
        if (warning != null) {
            Field[] fields = {
                new Field("Warned Member", String.format(
                    "%s (%s)",
                    warnedMember.getAsMention(),
                    warnedMember.getIdLong()), false),
                new Field("Warned Member", String.format(
                    "%s (%s)",
                    warnerMember.getAsMention(),
                    warnerMember.getIdLong()), false),
                new Field("Reason", reason, false),
                new Field("Warning Id", Integer.toString(warning.getWarningId()), false)
            };

            /* Send embed */
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createSuccessEmbed("Member Warned", warnedMember.getAsMention() + " has been warned", fields)
            ).queue();
            return;
        }
        
        /* Error response */
        event.getHook().sendMessageEmbeds(
            EmbedFactory.createWarningEmbed("Database Access Error", "Failed to warn member. Contact the the bot developer.")
        ).queue();
    }    
}
