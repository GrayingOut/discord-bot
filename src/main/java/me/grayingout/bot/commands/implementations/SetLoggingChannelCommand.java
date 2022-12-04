package me.grayingout.bot.commands.implementations;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.guildconfig.ConfigDatabase;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command used to set the channel the
 * bot uses for logging - restricted to MANAGE_SERVER
 */
public final class SetLoggingChannelCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("set-logging-channel", "Sets the channel the bot uses for logging")
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER))
            .addOption(OptionType.CHANNEL, "channel", "The channel to use", true)
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        /* Get the channel and check it is a GuildMessageChannel */
        Channel channel = event.getOption("channel").getAsChannel();
        if (!(channel instanceof GuildMessageChannel)) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Invalid Argument", "`channel` must be a text channel")
            ).queue();;
            return;
        }

        /* Update the logging channel */
        boolean success = ConfigDatabase.updateLoggingChannelId(event.getGuild(), (GuildMessageChannel) channel);
        if (!success) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Database Access Error", "Failed to update database. Contact the bot developer.")
            ).queue();;
            return;
        }

        /* Success */
        event.getHook().sendMessageEmbeds(
            EmbedFactory.createSuccessEmbed("Logging Channel Updated", "The bot will now log messages in " + channel.getAsMention())
        ).queue();
    }
}
