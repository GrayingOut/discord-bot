package me.grayingout.bot.commands.implementations;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.WarningsDatabase;
import me.grayingout.database.objects.MemberWarning;
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
        Member member = event.getOption("member").getAsMember();
        Member moderator = event.getMember();
        
        /* Default reason */
        String reason = "<no reason provided>";
        
        /* Check if reason provided */
        if (event.getOption("reason") != null) {
            reason = event.getOption("reason").getAsString();
        }

        /* Put the warning into the database */
        MemberWarning warning = WarningsDatabase.putWarning(
            member,
            moderator,
            reason);
        
        /* Send response */
        if (warning != null) {
            Field[] fields = {
                new Field("Member", String.format(
                    "%s (%s)",
                    member.getAsMention(),
                    member.getIdLong()), false),
                new Field("Moderator", String.format(
                    "%s (%s)",
                    moderator.getAsMention(),
                    moderator.getIdLong()), false),
                new Field("Reason", reason, false),
                new Field("Warning Id", Integer.toString(warning.getWarningId()), false)
            };

            /* Send embed */
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createSuccessEmbed("Member Warned", member.getAsMention() + " has been warned", fields)
            ).queue();
            return;
        }
        
        /* Error response */
        event.getHook().sendMessageEmbeds(
            EmbedFactory.createWarningEmbed("Database Access Error", "Failed to warn member. Contact the the bot developer.")
        ).queue();
    }    
}
