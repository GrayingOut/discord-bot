package me.grayingout.bot.commands.implementations.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import me.grayingout.bot.audioplayer.GuildAudioPlayer;
import me.grayingout.bot.audioplayer.GuildAudioPlayerManager;
import me.grayingout.bot.commands.BotCommand;
import me.grayingout.util.Audio;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command to get the currently playing audio
 */
public final class PlayingCommand extends BotCommand {

    /**
     * The base string for the progress string
     */
    private static final String PROGRESS_STRING_BASE = "--------------------";

    @Override
    public CommandData getCommandData() {
        return Commands.slash("playing", "Get the currently playing audio");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        if (!Audio.checkValidCommandExecutionState(event, true)) {
            return;
        }

        GuildAudioPlayer guildAudioPlayer = GuildAudioPlayerManager
            .getInstance()
            .getGuildAudioPlayer(event.getGuild());
        
        /* Get the playing audio track */
        AudioTrack track = guildAudioPlayer.getPlayingAudioTrack();

        if (track == null) {
            event.getHook().sendMessageEmbeds(EmbedFactory.createGenericEmbed(
                "📀 Currently Playing",
                "There is not track currently playing"
            )).queue();
            return;
        }

        event.getHook().sendMessageEmbeds(EmbedFactory.createGenericEmbed(
            "📀 Currently Playing",
            "",
            new Field[] {
                new Field("Title", track.getInfo().title, false),
                new Field("Author", track.getInfo().author, false),
                new Field("Progress", getPlayProgressString(track), false)
            }
        )).queue();
    }

    /**
     * Creates the embed progress string
     * 
     * @param track The track to get the play progress of
     * @return The string
     */
    private final String getPlayProgressString(AudioTrack track) {
        StringBuilder progressBarBuilder = new StringBuilder();

        long trackDurationSeconds = track.getDuration()/1000;
        long trackPositionSeconds = track.getPosition()/1000;

        double progress = trackPositionSeconds/(trackDurationSeconds * 1.0);

        progressBarBuilder.append(PROGRESS_STRING_BASE);
        progressBarBuilder.setCharAt((int) Math.floor(progress*PROGRESS_STRING_BASE.length()), 'o');

        /* Format the timings */
        String durationString = String.format(
            "%02d:%02d:%02d",
            (long)(trackDurationSeconds/3600),
            (long)(trackDurationSeconds%3600)/60,
            (long)(trackDurationSeconds%60));
        String positionString = String.format(
            "%02d:%02d:%02d",
            (long)(trackPositionSeconds/3600),
            (long)(trackPositionSeconds%3600)/60,
            (long)(trackPositionSeconds%60));
        
        progressBarBuilder.append("\n`" + positionString + "/" + durationString + "`");

        return progressBarBuilder.toString().replaceAll("-", ":heavy_minus_sign:").replace("o", ":black_circle:");
    }
}
