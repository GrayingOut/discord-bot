package me.grayingout.bot.audioplayer;

import java.util.HashMap;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.dv8tion.jda.api.entities.Guild;

/**
 * Manages the {@code GuildAudioPlayer}s
 */
public final class GuildAudioPlayerManager {

    /**
     * The singleton instance
     */
    private static GuildAudioPlayerManager guildAudioPlayerManager;

    /**
     * Stores the {@code GuildAudioPlayer} for each guild
     */
    private final HashMap<Long, GuildAudioPlayer> guildAudioPlayers;

    /**
     * The main {@code AudioPlayerManager}
     */
    private final AudioPlayerManager audioPlayerManager;

    private GuildAudioPlayerManager() {
        guildAudioPlayers = new HashMap<>();
        audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    }

    /**
     * Gets the {@code GuildAudioPlayerManager} singleton
     * 
     * @return The singleton
     */
    public static final GuildAudioPlayerManager getInstance() {
        if (guildAudioPlayerManager == null) {
            guildAudioPlayerManager = new GuildAudioPlayerManager();
        }

        return guildAudioPlayerManager;
    }
    
    /**
     * Gets the {@code GuildAudioPlayer} for a guild
     * 
     * @param guild The guild
     * @return The {@code GuildAudioPlayer}
     */
    public final GuildAudioPlayer getGuildAudioPlayer(Guild guild) {
        if (guildAudioPlayers.get(guild.getIdLong()) == null) {
            GuildAudioPlayer guildAudioPlayer = new GuildAudioPlayer(audioPlayerManager);
            
            /* Set the audio send handler on the guild */
            guild.getAudioManager().setSendingHandler(guildAudioPlayer.getAudioPlayerSendHandler());
            
            guildAudioPlayers.put(guild.getIdLong(), guildAudioPlayer);
        }

        return guildAudioPlayers.get(guild.getIdLong());
    }

    /**
     * Returns the {@code AudioPlayerManager} instance
     * 
     * @return The {@code AudioPlayerManager}
     */
    public final AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }
}
