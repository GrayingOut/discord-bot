package me.grayingout.bot.audioplayer.handler;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * The result of loading an audio on a {@code GuildAudioManager}
 */
public class AudioLoadResult {

    /**
     * The type of this result
     */
    private final AudioLoadResultType resultType;
    
    /**
     * The track loaded
     */
    private final AudioTrack audioTrack;
    
    /**
     * Creates a new {@code AudioLoadResult}
     * 
     * @param resultType The type of result
     * @param audioTrack The track loaded if successful
     */
    public AudioLoadResult(AudioLoadResultType resultType, AudioTrack audioTrack) {
        this.resultType = resultType;
        this.audioTrack = audioTrack;
    }

    /**
     * Gets the {@code AudioLoadResultType} of this
     * load result
     * 
     * @return The {@code AudioResultType}
     */
    public final AudioLoadResultType getResultType() {
        return resultType;
    }

    /**
     * Gets the {@code AudioTrack} loaded, or {@code null} if
     * no track was loaded
     * 
     * @return The {@code AudioTrack} or {@code null}
     */
    public final AudioTrack getLoadedAudioTrack() {
        return audioTrack;
    }
}
