package me.grayingout.bot.audioplayer;

import java.util.HashMap;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

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
     * Plays an audio from a URL
     * 
     * @param member The member that added the audio
     * @param channel The channel the /play command was sent in
     * @param url The URL
     */
    public final void playAudio(Member member, GuildMessageChannel channel, String url) {
        GuildAudioPlayer guildAudioPlayer = getGuildAudioPlayer(channel.getGuild());

        audioPlayerManager.loadItemOrdered(guildAudioPlayer, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                guildAudioPlayer.queue(track);
                channel.sendMessageEmbeds(
                    EmbedFactory.createSuccessEmbed(
                        "Audio Added to Queue",
                        "Audio has been added to the audio queue",
                        new Field[] {
                            new Field("Title", track.getInfo().title, false),
                            new Field("Author", track.getInfo().author, false),
                            new Field("Added by", member.getAsMention(), false)
                        }
                    )
                ).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                channel.sendMessageEmbeds(
                    EmbedFactory.createErrorEmbed(
                        "Failed to add Audio",
                        e.getMessage()
                    )
                ).queue();
            }

            @Override
            public void noMatches() {
                channel.sendMessageEmbeds(
                    EmbedFactory.createWarningEmbed(
                        "Failed to add Audio",
                        "No match was found for `" + url + "`"
                    )
                ).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
            }
        });
    }
}
