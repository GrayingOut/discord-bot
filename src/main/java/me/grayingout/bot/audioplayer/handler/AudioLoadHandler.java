package me.grayingout.bot.audioplayer.handler;

import java.util.concurrent.CompletableFuture;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import me.grayingout.bot.audioplayer.GuildAudioPlayer;

/**
 * The handler class for handling the result of loading
 * and audio track
 */
public final class AudioLoadHandler implements AudioLoadResultHandler {

    /**
     * The future that contains the load result
     */
    private final CompletableFuture<AudioLoadResult> future;

    /**
     * The {@code GuildAudioPlayer} this result is for
     */
    private final GuildAudioPlayer guildAudioPlayer;

    /**
     * Creates a new {@code AudioLoadHandler}
     * 
     * @param guildAudioPlayer The {@code GuildAudioPlayer} this result is for
     */
    public AudioLoadHandler(GuildAudioPlayer guildAudioPlayer) {
        this.guildAudioPlayer = guildAudioPlayer;
        future = new CompletableFuture<>();
    }

    /**
     * Waits for the load handler to get a result
     */
    public final AudioLoadResult awaitLoadResult() {
        return future.join();
    }

    @Override
    public void loadFailed(FriendlyException e) {
        /* Something went wrong */
        e.printStackTrace();
        future.complete(new AudioLoadResult(
            AudioLoadResultType.ERROR,
            null
        ));
    }

    @Override
    public void noMatches() {
        /* No match found */
        future.complete(new AudioLoadResult(
            AudioLoadResultType.NO_MATCH,
            null
        ));
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        /* Playlist was found; check if for search result */
        if (playlist.isSearchResult()) {
            if (playlist.getTracks().size() > 0) {
                AudioTrack track = playlist.getTracks().get(0);

                if (guildAudioPlayer.queueContains(track)) {
                    /* Track already added */
                    future.complete(new AudioLoadResult(
                        AudioLoadResultType.ALREADY_ADDED,
                        track
                    ));
                    return;
                }

                /* Track was found; queue for playing */
                future.complete(new AudioLoadResult(
                    AudioLoadResultType.TRACK_LOADED,
                    track
                ));
            }
        }
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        if (guildAudioPlayer.queueContains(track)) {
            /* Track already added */
            future.complete(new AudioLoadResult(
                AudioLoadResultType.ALREADY_ADDED,
                track
            ));
            return;
        }

        /* Track was found; queue for playing */
        future.complete(new AudioLoadResult(
            AudioLoadResultType.TRACK_LOADED,
            track
        ));
    }
}
