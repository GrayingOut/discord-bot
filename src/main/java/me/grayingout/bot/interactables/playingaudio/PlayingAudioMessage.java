package me.grayingout.bot.interactables.playingaudio;

import java.util.Arrays;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import me.grayingout.bot.audioplayer.GuildAudioPlayer;
import me.grayingout.bot.audioplayer.GuildAudioPlayerManager;
import me.grayingout.util.Audio;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

/**
 * Wrapper for a playing audio message
 */
public final class PlayingAudioMessage {

    /**
     * The base string for the progress string
     */
    private static final String PROGRESS_STRING_BASE = "----------";

    /**
     * The audio queue message
     */
    private final Message message;

    /**
     * Creates a new {@code PlayingAudioMessage}
     * 
     * @param message The associated message
     */
    public PlayingAudioMessage(Message message) {
        this.message = message;
    }

    /**
     * Gets the message associated with this {@code AudioQueueMessage}
     * 
     * @return The message
     */
    public final Message getMessage() {
        return message;
    }

    /**
     * Refreshes the message
     */
    public final void refresh() {
        MessageEditData data = new MessageEditBuilder()
            .setEmbeds(createPlayingAudioMessageEmbed(message.getGuild()))
            .setActionRow(getActionRowButtons(message.getGuild()))
            .build();
        
        /* Edit the message */
        message.editMessage(data).queue();
    }

    /**
     * Creates the {@code MessageCreateData} for a playing audio message
     * 
     * @param guild The guild the message is for
     * @return The {@code MessageCreateData}
     */
    public static final MessageCreateData createPlayingAudioMessageData(Guild guild) {
        return new MessageCreateBuilder()
            .setEmbeds(createPlayingAudioMessageEmbed(guild))
            .setActionRow(getActionRowButtons(guild))
            .build();
    }

    /**
     * Creates the embed for the message
     * 
     * @param guild The guild the embed is for
     * @return The built embed
     */
    private static final MessageEmbed createPlayingAudioMessageEmbed(Guild guild) {
        GuildAudioPlayer guildAudioPlayer = GuildAudioPlayerManager
            .getInstance()
            .getGuildAudioPlayer(guild);
        
        /* Get the playing audio track */
        AudioTrack track = guildAudioPlayer.getPlayingAudioTrack();

        if (track == null) {
            return EmbedFactory.createGenericEmbed(
                "📀 Currently Playing",
                "There is not track currently playing",
                new Field[] {
                    new Field("Looping", ""+guildAudioPlayer.isLooping(), false)
                }
            );
        }

        return EmbedFactory.createGenericEmbed(
            "📀 Currently Playing",
            "",
            new Field[] {
                new Field("Title", track.getInfo().title, false),
                new Field("Author", track.getInfo().author, false),
                new Field("Progress", getPlayProgressString(track), false),
                new Field("Looping", ""+guildAudioPlayer.isLooping(), false)
            }
        );
    }

    /**
     * Creates the action row buttons for the message
     * 
     * @param guild The guild the action row is for
     * @return The list of action row buttons
     */
    private static final List<Button> getActionRowButtons(Guild guild) {
        Button refreshButton = Button.secondary("playing_audio_refresh", "Refresh");
        Button loopButton = Button.primary("playing_audio_loop", "Toggle Loop");
        Button skipButton = Button.danger("playing_audio_skip", "Skip");

        if (GuildAudioPlayerManager.getInstance().getGuildAudioPlayer(guild).getPlayingAudioTrack() == null) {
            skipButton = skipButton.asDisabled();
        }

        return Arrays.asList(refreshButton, loopButton, skipButton);
    }

    /**
     * Creates the embed progress string
     * 
     * @param track The track to get the play progress of
     * @return The string
     */
    private static final String getPlayProgressString(AudioTrack track) {
        StringBuilder progressBarBuilder = new StringBuilder();

        long trackDurationSeconds = track.getDuration()/1000;
        long trackPositionSeconds = track.getPosition()/1000;

        double progress = trackPositionSeconds/(trackDurationSeconds * 1.0);

        progressBarBuilder.append(PROGRESS_STRING_BASE);
        progressBarBuilder.setCharAt((int) Math.floor(progress*PROGRESS_STRING_BASE.length()), 'o');
        
        progressBarBuilder.append("\n`" + Audio.formatAudioTrackTime(trackPositionSeconds) + "/" + Audio.formatAudioTrackTime(trackDurationSeconds) + "`");

        return progressBarBuilder.toString().replaceAll("-", ":heavy_minus_sign:").replace("o", ":black_circle:");
    }
}
