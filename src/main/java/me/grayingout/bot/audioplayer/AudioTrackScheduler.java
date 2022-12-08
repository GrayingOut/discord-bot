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
     * Creates a new {@code AudioTrackScheduler} for an
     * {@code AudioPlayer}
     * 
     * @param audioPlayer The audio player instance
     */
    public AudioTrackScheduler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.trackQueue = new LinkedBlockingQueue<>();
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
        }
    }

    /**
     * Start the next track
     */
    public final void nextTrack() {
        /* Starts the next track */
        audioPlayer.startTrack(trackQueue.poll(), false);
    }
    
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}
