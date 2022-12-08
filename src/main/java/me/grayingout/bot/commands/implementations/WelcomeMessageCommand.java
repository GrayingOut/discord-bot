package me.grayingout.bot.commands.implementations;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.accessors.DatabaseAccessorManager;
import me.grayingout.database.entities.GuildWelcomeMessage;
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
 * A slash command for setting and removing the welcome message
 * channel, and setting the welcome message - restricted to
 * {@code Permission.MANAGE_SERVER}
 */
public final class WelcomeMessageCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("welcome-message", "Configure the welcome message")
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER))
            .addSubcommands(
                new SubcommandData("set-channel", "Set the welcome channel")
                    .addOption(OptionType.CHANNEL, "channel", "The channel to use", true),
                new SubcommandData("remove-channel", "Remove the welcome channel"),
                new SubcommandData("set-message", "Set the welcome message")
                    .addOption(OptionType.STRING, "message", "The welcome message", true),
                new SubcommandData("show-config", "Show the current config for welcome messages")
            );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        switch (event.getSubcommandName()) {
            case "set-channel": {
                Channel channel = event.getOption("channel").getAsChannel();
                if (!(channel instanceof GuildMessageChannel)) {
                    event.getHook().sendMessageEmbeds(
                        EmbedFactory.createWarningEmbed("Invalid Argument", "`channel` must be a text channel")
                    ).queue();
                    return;
                }

                DatabaseAccessorManager
                    .getConfigurationDatabaseAccessor()
                    .updateWelcomeChannelId(
                        event.getGuild(),
                        (GuildMessageChannel) event.getOption("channel").getAsChannel()
                    );
                
                /* Success */
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createSuccessEmbed("Welcome Channel Updated", "The bot will now send welcome messages in " + channel.getAsMention())
                ).queue();
                break;
            }
            case "remove-channel": {
                DatabaseAccessorManager
                    .getConfigurationDatabaseAccessor()
                    .removeWelcomeChannelId(event.getGuild());
                
                /* Success */
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createSuccessEmbed("Welcome Channel Updated", "The bot will no longer send welcome messages")
                ).queue();
                break;
            }
            case "set-message": {
                String welcomeMessage = event.getOption("message").getAsString().trim();

                DatabaseAccessorManager
                    .getConfigurationDatabaseAccessor()
                    .setWelcomeMessage(event.getGuild(), welcomeMessage);
                
                /* Success */
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createSuccessEmbed("Welcome Message Updated", "The welcome message has been updated to `" + welcomeMessage + "`")
                ).queue();
                break;
            }
            case "show-config": {
                GuildWelcomeMessage welcomeMessage = GuildWelcomeMessage.getGuildWelcomeMessage(event.getGuild());
                
                Field[] fields = new Field[] {
                    new Field("Channel", welcomeMessage.getWelcomeChannel() == null ? "<none>" : welcomeMessage.getWelcomeChannel().getAsMention(), false),
                    new Field("Message", welcomeMessage.getMessage(), false)
                };
                
                /* Success */
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createGenericEmbed(":gear: Welcome Message Config", "", fields)
                ).queue();
                break;
            }
            default:
                throw new RuntimeException("Unhandled /welcome-message subcommand: " + event.getSubcommandName());
        }
    }
}
