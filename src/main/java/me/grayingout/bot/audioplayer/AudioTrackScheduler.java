package me.grayingout.bot.audioplayer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

/**
 * Event listener for the {@code AudioPlayer} instance
 */
public class AudioTrackScheduler extends AudioEventAdapter {

    /**
     * The audio player
     */
    private final AudioPlayer audioPlayer;

    /**
     * Stores a queue of audio tracks to play
     */
    private final BlockingQueue<AudioTrack> trackQueue;

    /**
     * Stores the current playing track
     */
    private AudioTrack playingTrack;

    /**
     * Creates a new {@code AudioTrackScheduler} for an
     * {@code AudioPlayer}
     * 
     * @param audioPlayer The audio player instance
     */
    public AudioTrackScheduler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.trackQueue = new LinkedBlockingQueue<>();
        playingTrack = null;
    }

    /**
     * Queues an audio track for playing immediately, if queue
     * is empty, else adding it to the queue
     * 
     * @param audioTrack The audio track
     */
    public final void queue(AudioTrack audioTrack) {
        /* Attempt to immediately play track */
        boolean playStarted = audioPlayer.startTrack(audioTrack, true);
        
        if (!playStarted) {
            trackQueue.offer(audioTrack);
            return;
        }
        playingTrack = audioTrack;
    }

    /**
     * Clears the {@code AudioTrack} queue
     */
    public final void clear() {
        trackQueue.clear();
    }

    /**
     * Stops the currently playing {@code AudioTrack}
     */
    public final void stop() {
        audioPlayer.stopTrack();
    }

    /**
     * Start the next track
     */
    public final void nextTrack() {
        /* Starts the next track */
        AudioTrack track = trackQueue.poll();
        audioPlayer.startTrack(track, false);
        playingTrack = track;
    }

    /**
     * Returns the current audio track playing
     * 
     * @return The audio track, or {@code null} if not track is playing
     */
    public final AudioTrack getPlayingTrack() {
        return playingTrack;
    }
    
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        playingTrack = null;
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}
