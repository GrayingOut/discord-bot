package me.grayingout.bot.commands.implementations.audio;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.util.Audio;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command to have the bot leave its current voice channel
 */
public final class LeaveCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("leave", "Make the bot leave its current voice channel");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        /* Check the execution environment */
        if (!Audio.checkValidCommandExecutionState(event, true)) {
            return;
        }

        /* Leave channel */
        event.getGuild().getAudioManager().closeAudioConnection();

        /* Send success response */
        event.getHook().sendMessageEmbeds(
            EmbedFactory.createSuccessEmbed(
                "Left Channel",
                "Bot has disconnected"
            )
        ).queue();
    }
}
