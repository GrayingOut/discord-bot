package me.grayingout.bot.commands.implementations;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.accessors.DatabaseAccessorManager;
import me.grayingout.database.entities.GuildLoggingChannel;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
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
                new SubcommandData("remove-channel", "Remove the logging channel"),
                new SubcommandData("enable-logging", "Enable a logging type")
                    .addOption(OptionType.STRING, "type", "Type of logging to enable", true),
                new SubcommandData("disable-logging", "Disable a logging type")
                    .addOption(OptionType.STRING, "type", "Type of logging to disable", true),
                new SubcommandData("show-config", "Shows the current logging setup")
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
            case "enable-logging": {
                /* Check logging type exists */
                String type = event.getOption("type").getAsString().toUpperCase();

                GuildLoggingChannel.LoggingEventType loggingType = null;

                try {
                    loggingType = GuildLoggingChannel.LoggingEventType.valueOf(type);
                } catch (IllegalArgumentException e) {
                    /* No type exists */
                    event.getHook().sendMessageEmbeds(
                        EmbedFactory.createErrorEmbed("Invalid Logging Type", "Logging type `" + type + "` is not recognised")
                    ).queue();
                    return;
                }

                DatabaseAccessorManager.getConfigurationDatabaseAccessor()
                    .enableLoggingType(event.getGuild(), loggingType);
            
                /* Success */
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createSuccessEmbed("Logging Type Enabled", "The bot will now log logs of type `" + type + "`")
                ).queue();
                break;
            }
            case "disable-logging": {
                /* Check logging type exists */
                String type = event.getOption("type").getAsString().toUpperCase();

                GuildLoggingChannel.LoggingEventType loggingType = null;

                try {
                    loggingType = GuildLoggingChannel.LoggingEventType.valueOf(type);
                } catch (IllegalArgumentException e) {
                    /* No type exists */
                    event.getHook().sendMessageEmbeds(
                        EmbedFactory.createErrorEmbed("Invalid Logging Type", "Logging type `" + type + "` is not recognised")
                    ).queue();
                    return;
                }

                DatabaseAccessorManager.getConfigurationDatabaseAccessor()
                    .disableLoggingType(event.getGuild(), loggingType);
            
                /* Success */
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createSuccessEmbed("Logging Type Disabled", "The bot will no longer log logs of type `" + type + "`")
                ).queue();
                break;
            }
            case "show-config":
                GuildLoggingChannel loggingChannel = GuildLoggingChannel
                    .getGuildLoggingChannel(event.getGuild());

                /* Create the logging fields */
                Field[] fields = new Field[2];
                fields[0] = new Field(
                    "Logging Channel",
                    loggingChannel.getLoggingChannel() == null ? "<none>" : loggingChannel.getLoggingChannel().getAsMention(),
                    false);
                
                /* Construct which logging types are enabled */
                StringBuilder enabledLoggingTypes = new StringBuilder();
                for (GuildLoggingChannel.LoggingEventType type : GuildLoggingChannel.LoggingEventType.values()) {
                    if (loggingChannel.isLoggingTypeEnabled(type)) {
                        enabledLoggingTypes.append(":green_circle: `" + type.name() + "`\n");
                        continue;
                    }
                    enabledLoggingTypes.append(":red_circle: `" + type.name() + "`\n");
                }
                fields[1] = new Field("Enabled Logging Types", enabledLoggingTypes.toString(), false);

                /* Send message */
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createGenericEmbed(
                        ":gear: Logging Channel Config",
                        "",
                        fields)
                ).queue();
                break;
            default:
                throw new RuntimeException("Unhandled /logging subcommand: " + event.getSubcommandName());
        }

    }
}
