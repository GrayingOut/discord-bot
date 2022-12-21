package me.grayingout.bot.audioplayer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import me.grayingout.bot.audioplayer.skip.GuildSkipAudioManager;
import net.dv8tion.jda.api.entities.Guild;

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
     * Whether looping of the current track is enabled
     */
    private boolean loopCurrentTrack;

    /**
     * The guild this {@code AudioTrackScheduler} belongs to
     */
    private final Guild guild;

    /**
     * Creates a new {@code AudioTrackScheduler} for an
     * {@code AudioPlayer}
     * 
     * @param guild The guild this {@code AudioTrackScheduler} belongs to
     * @param audioPlayer The audio player instance
     */
    public AudioTrackScheduler(Guild guild, AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.trackQueue = new LinkedBlockingQueue<>();
        loopCurrentTrack = false;
        this.guild = guild;
    }

    /**
     * Queues an audio track for playing immediately, if queue
     * is empty, else adding it to the queue
     * 
     * @param audioTrack The audio track
     */
    public final void queue(AudioTrack audioTrack) {
        audioTrack.makeClone();

        /* Attempt to immediately play track */
        boolean playStarted = audioPlayer.startTrack(audioTrack, true);
        
        if (!playStarted) {
            trackQueue.offer(audioTrack);
            return;
        }
    }

    /**
     * Returns if looping is enabled
     * 
     * @return If looping is enabled
     */
    public final boolean isLoopingEnabled() {
        return loopCurrentTrack;
    }

    /**
     * Sets whether looping is enabled
     * 
     * @param enabled If looping is enabled
     */
    public final void setLoopingEnabled(boolean enabled) {
        loopCurrentTrack = enabled;
    }

    /**
     * Gets the queue of tracks to be played
     * 
     * @return The array of upcoming {@code AudioTrack}s
     */
    public final AudioTrack[] getQueue() {
        return trackQueue.toArray(new AudioTrack[] {});
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
    }

    /**
     * Returns the current audio track playing
     * 
     * @return The audio track, or {@code null} if not track is playing
     */
    public final AudioTrack getPlayingTrack() {
        return audioPlayer.getPlayingTrack();
    }
    
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.equals(AudioTrackEndReason.LOAD_FAILED)) {
            /* Clear current skip vote */
            GuildSkipAudioManager.getInstance().getGuildSkipAudio(guild).resetVoteSkips();
            return;
        }

        /* Check if looping enabled, and audio track wasn't stopped (/skip, /stop) */
        if (loopCurrentTrack && !endReason.equals(AudioTrackEndReason.STOPPED)) {
            player.startTrack(track.makeClone(), false);
            return;
        }
        
        /* Clear current skip vote */
        GuildSkipAudioManager.getInstance().getGuildSkipAudio(guild).resetVoteSkips();

        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}
