package me.grayingout.bot.commands.implementations;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.accessor.DatabaseAccessorManager;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A slash command used to set the channel the
 * bot uses for logging - restricted to {@code Permission.MANAGE_SERVER}
 */
public final class LoggingCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("logging", "Configure guild logging")
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER))
            .addSubcommands(
                new SubcommandData("set-channel", "Set the channel used for logging")
                    .addOption(OptionType.CHANNEL, "channel", "The channel to use", true),
                new SubcommandData("remove-channel", "Remove the logging channel")
            )
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        switch (event.getSubcommandName()) {
            case "set-channel":
                /* Get the channel and check it is a GuildMessageChannel */
                Channel channel = event.getOption("channel").getAsChannel();
                if (!(channel instanceof GuildMessageChannel)) {
                    event.getHook().sendMessageEmbeds(
                        EmbedFactory.createWarningEmbed("Invalid Argument", "`channel` must be a text channel")
                    ).queue();
                    return;
                }
            
                /* Update the logging channel */
                DatabaseAccessorManager.getConfigurationDatabaseAccessor()
                    .updateLoggingChannelId(event.getGuild(), (GuildMessageChannel) channel);
            
                /* Success */
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createSuccessEmbed("Logging Channel Updated", "The bot will now log messages in " + channel.getAsMention())
                ).queue();
                break;
            case "remove-channel":
                /* Remove the logging channel */
                DatabaseAccessorManager.getConfigurationDatabaseAccessor()
                    .removeLoggingChannel(event.getGuild());
            
                /* Success */
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createSuccessEmbed("Logging Channel Removed", "The bot will no longer log messages")
                ).queue();
                break;
            default:
                throw new RuntimeException("Unhandled /logging subcommand: " + event.getSubcommandName());
        }

    }
}
