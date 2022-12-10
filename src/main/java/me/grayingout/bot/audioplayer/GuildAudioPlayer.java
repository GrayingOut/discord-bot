package me.grayingout.bot.audioplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

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
     * Queues an audio track for playing, or plays immediately
     * if the queue is empty
     * 
     * @param track The audio track
     */
    public final void queue(AudioTrack track) {
        audioTrackScheduler.queue(track);
    }

    /**
     * Stops the currently playing track
     */
    public final void stop() {
        if (audioTrackScheduler.getPlayingTrack() != null) {
            audioTrackScheduler.getPlayingTrack().stop();
        }
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
