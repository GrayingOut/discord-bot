package me.grayingout.bot.audioplayer;

import java.util.Arrays;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import me.grayingout.bot.audioplayer.handler.AudioLoadHandler;
import me.grayingout.bot.audioplayer.handler.AudioLoadResult;
import me.grayingout.bot.audioplayer.handler.AudioLoadResultType;

/**
 * Manages the playing of audio in a guild
 */
public final class GuildAudioPlayer {

    /**
     * The guild's audio player
     */
    private final AudioPlayer audioPlayer;

    /**
     * The guild's audio track scheduler, which is used for queueing
     * tracks
     */
    private final AudioTrackScheduler audioTrackScheduler;

    /**
     * The guild's audio player send handler, which is used
     * to send audio to the guild
     */
    private final AudioPlayerSendHandler audioPlayerSendHandler;

    /**
     * Creates a new {@code GuildAudioPlayerManager}
     * 
     * @param audioPlayerManager The audio player manager
     */
    public GuildAudioPlayer(AudioPlayerManager audioPlayerManager) {
        audioPlayer = audioPlayerManager.createPlayer();
        audioTrackScheduler = new AudioTrackScheduler(audioPlayer);
        audioPlayer.addListener(audioTrackScheduler);
        audioPlayerSendHandler = new AudioPlayerSendHandler(audioPlayer);
    }

    /**
     * Returns if the audio track contains the specific audio
     * track
     * 
     * @param track The audio track to check
     * @return If it is in the queue
     */
    public final boolean queueContains(AudioTrack track) {
        return Arrays.stream(audioTrackScheduler.getQueue())
            .filter(t -> t.getIdentifier().equals(track.getIdentifier()))
            .count() > 0;
    }

    /**
     * Returns if looping is enabled
     */
    public final boolean isLooping() {
        return audioTrackScheduler.isLoopingEnabled();
    }
    
    /**
     * Disables looping of the current track
     */
    public final void disableLoop() {
        audioTrackScheduler.setLoopingEnabled(false);
    }

    /**
     * Enables looping of the current track
     */
    public final void enableLoop() {
        audioTrackScheduler.setLoopingEnabled(true);
    }

    /**
     * Gets the currently playing {@code AudioTrack},
     * or {@code null} if not track is playing
     * 
     * @return The playing audio {@code AudioTrack} or {@code null}
     */
    public final AudioTrack getPlayingAudioTrack() {
        return audioTrackScheduler.getPlayingTrack();
    }

    /**
     * Gets the list of upcoming {@code AudioTrack}s to
     * be played
     * 
     * @return The list of upcoming {@code AudioTrack}s
     */
    public final AudioTrack[] getAudioTrackQueue() {
        return audioTrackScheduler.getQueue();
    }

    /**
     * Stops the currently playing {@code AudioTrack} if one
     * is playing
     */
    public final void stopPlaying() {
        audioTrackScheduler.stop();
    }

    /**
     * Clears the {@code AudioTrackScheduler}'s queue
     */
    public final void clearQueue() {
        audioTrackScheduler.clear();
    }

    /**
     * Ends the currently playing audio and starts the
     * next audio
     */
    public final void skip() {
        audioTrackScheduler.nextTrack();
    }

    /**
     * Queues an audio track for playing, by getting it
     * from the provided URL using the provided audio
     * player manager
     * 
     * @param manager The audio player manager
     * @param url     The URL of the audio
     * @return The {@code AudioLoadResult} of attempting to queue the url
     */
    public final AudioLoadResult queueAudioByURL(String url) {
        AudioLoadHandler handler = new AudioLoadHandler(this);

        GuildAudioPlayerManager
            .getInstance()
            .getAudioPlayerManager()
            .loadItemOrdered(this, url, handler);
        
        AudioLoadResult result = handler.awaitLoadResult();

        if (result.getResultType().equals(AudioLoadResultType.TRACK_LOADED)) {
            audioTrackScheduler.queue(result.getLoadedAudioTrack());
        }
        
        return result;
    }

    /**
     * Queues an audio track for playing, by searching
     * for it on YouTube using the provided audio
     * player manager
     * 
     * @param manager The audio player manager
     * @param query   The search query
     * @return The {@code AudioLoadResult} of attempting to queue the query
     */
    public final AudioLoadResult queueAudioByYTSearch(String query) {
        AudioLoadHandler handler = new AudioLoadHandler(this);

        GuildAudioPlayerManager
            .getInstance()
            .getAudioPlayerManager()
            .loadItemOrdered(this, "ytsearch:" + query, handler);
        
        AudioLoadResult result = handler.awaitLoadResult();

        if (result.getResultType().equals(AudioLoadResultType.TRACK_LOADED)) {
            audioTrackScheduler.queue(result.getLoadedAudioTrack());
        }
        
        return result;
    }

    /**
     * Gets the guild's audio player send handler
     * 
     * @return The {@code AudioPlayerSendHandler}
     */
    public final AudioPlayerSendHandler getAudioPlayerSendHandler() {
        return audioPlayerSendHandler;
    }
}
