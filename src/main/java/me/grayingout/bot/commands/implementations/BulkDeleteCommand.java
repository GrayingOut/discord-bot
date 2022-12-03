package me.grayingout.bot.commands.implementations;

import java.util.List;
import java.util.concurrent.TimeUnit;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command used to delete multiple messages
 * at the same time - restricted to {@code Permission.MESSAGE_MANAGE}
 */
public class BulkDeleteCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands
            .slash("bulk-delete", "Delete a specific number of messages from a channel")
            .addOption(OptionType.INTEGER, "count", "Number of previous messages to delete. Between 2 and 100", true)
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        int count = -1;
        try {
            count = event.getOption("count").getAsInt();
        } catch (ArithmeticException e) {
            /* Invalid integer */
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createExceptionEmbed(e, "The provided `number` cannot be processed by the application")
            ).queue();
            return;
        }

        /* Check if count is valid */
        if (count < 2 || count > 100) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Invalid Argument", "`number` must be between 2 and 100")
            ).queue();
            return;
        }

        /* Get the channel */
        GuildMessageChannel channel = (GuildMessageChannel) event.getChannel();

        /* Get messages */
        List<Message> messages = channel.getIterableHistory().takeAsync(count).join();

        if (messages.size() < 2) {
            /* Cannot use bulk delete */
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Not Enough Messages", "Not enough messages in channel to perform a bulk delete")
            ).queue();
            return;
        }

        /* Delete messages */
        channel.deleteMessages(messages).queue();

        /* Send response and delete after 3 seconds */
        event.getHook().sendMessageEmbeds(
            EmbedFactory.createSuccessEmbed("Deleted " + messages.size() + " messages", "The messages have been successfully deleted")
        ).queue(message -> {
            message.delete().queueAfter(3, TimeUnit.SECONDS);
        });
    }    
}
