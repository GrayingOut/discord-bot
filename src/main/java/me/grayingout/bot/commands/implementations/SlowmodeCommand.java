package me.grayingout.bot.commands.implementations;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command to set the slowmode of a channel
 * in discord - restricted to {@code Permission.MANAGE_CHANNELS}
 */
public class SlowmodeCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands
            .slash("slowmode", "Set the slowmode of a channel")
            .addOption(OptionType.INTEGER, "seconds", "Duration of slowmode in seconds - 0 to disable", true)
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL))
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        int seconds = -1;
        try {
            seconds = event.getOption("seconds").getAsInt();
        } catch (ArithmeticException e) {
            /* Invalid integer */
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createExceptionEmbed(e, "The provided `seconds` cannot be processed by the application")
            ).queue();
            return;
        }

        /* Check if seconds is valid */
        if (seconds < 0 || seconds > 21600) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Invalid Argument", "`seconds` must be between 0 and 21600")
            ).queue();
            return;
        }

        /* Check channel type is supported */
        if (!event.getChannelType().equals(ChannelType.TEXT)) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Unsupported Channel", "Unsupported channel type: " + event.getChannelType())
            ).queue();
            return;
        }

        /* Get the channel */
        TextChannel channel = (TextChannel) event.getChannel();
        
        /* Set the slowmode */
        channel.getManager().setSlowmode(seconds).queue();

        if (seconds == 0) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createSuccessEmbed("Slowmode Disabled", "Slowmode has been disabled")
            ).queue();
            return;
        }

        event.getHook().sendMessageEmbeds(
            EmbedFactory.createSuccessEmbed("Slowmode Updated", "Slowmode has been updated `" + seconds + "s`")
        ).queue();
    }
}
