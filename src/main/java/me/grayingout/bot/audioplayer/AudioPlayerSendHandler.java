package me.grayingout.bot.audioplayer;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

/**
 * Used to send audio to the JDA
 */
public final class AudioPlayerSendHandler implements AudioSendHandler {

    /**
     * Audio player instance
     */
    private final AudioPlayer audioPlayer;

    /**
     * Holds a buffer of audio data to send
     */
    private final ByteBuffer audioBuffer;

    /**
     * Used for writing audio data to the buffer
     */
    private final MutableAudioFrame audioFrame;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        audioBuffer = ByteBuffer.allocate(1024);

        audioFrame = new MutableAudioFrame();
        audioFrame.setBuffer(audioBuffer);
    }

    @Override
    public boolean canProvide() {
        return audioPlayer.provide(audioFrame);
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        ((Buffer) audioBuffer).flip();

        return (ByteBuffer) audioBuffer;
    }
    
    @Override
    public boolean isOpus() {
        /* LavaPlayer is always Opus */
        return true;
    }
}
