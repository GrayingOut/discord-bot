package me.grayingout.bot.audioplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

/**
 * Manages the playing of audio in a guild
 */
public final class GuildAudioPlayer {

    /**
     * The guild's audio player
     */
    private final AudioPlayer audioPlayer;

    /**
     * The guild's audio track scheduler
     */
    /* default */ final AudioTrackScheduler audioTrackScheduler;

    /**
     * The guild's audio player send handler
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
     * Gets the guild's audio player send handler
     * 
     * @return The {@code AudioPlayerSendHandler}
     */
    public final AudioPlayerSendHandler getAudioPlayerSendHandler() {
        return audioPlayerSendHandler;
    }
}
