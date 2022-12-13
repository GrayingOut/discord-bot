package me.grayingout.bot.commands.implementations.audio;

import me.grayingout.bot.audioplayer.GuildAudioPlayer;
import me.grayingout.bot.audioplayer.GuildAudioPlayerManager;
import me.grayingout.bot.commands.BotCommand;
import me.grayingout.util.Audio;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command to skip a playing audio
 */
public final class SkipCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("skip", "Skip the currently playing audio");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        /* Check execution environment */
        if (!Audio.checkValidCommandExecutionState(event, true)) {
            return;
        }

        GuildAudioPlayer guildAudioPlayer = GuildAudioPlayerManager.getInstance()
            .getGuildAudioPlayer(event.getGuild());

        /* Check an audio is playing */
        if (guildAudioPlayer.getPlayingAudioTrack() == null) {
            event.getHook().sendMessageEmbeds(EmbedFactory.createWarningEmbed(
                "No Audio Playing",
                "There needs to be an audio playing to use this command"
            )).queue();
            return;
        }

        /* Skip the audio */
        guildAudioPlayer.skip();

        event.getHook().sendMessageEmbeds(EmbedFactory.createSuccessEmbed(
            "Audio Skipped",
            "The playing audio track has been skipped"
        )).queue();
    }
}
