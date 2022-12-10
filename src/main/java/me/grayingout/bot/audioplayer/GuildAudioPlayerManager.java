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
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

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
     * Search for an audio to play from a slash command
     * 
     * @param event The slash command event
     */
    public final void searchAudio(SlashCommandInteractionEvent event) {
        GuildAudioPlayer guildAudioPlayer = getGuildAudioPlayer(event.getGuild());

        audioPlayerManager.loadItemOrdered(
            guildAudioPlayer,
            "ytsearch:" + event.getOption("search").getAsString(),
            new AudioResultHandler(event));
    }

    /**
     * Plays an audio from a slash command
     * 
     * @param event The slash command event
     */
    public final void playAudio(SlashCommandInteractionEvent event) {
        GuildAudioPlayer guildAudioPlayer = getGuildAudioPlayer(event.getGuild());

        audioPlayerManager.loadItemOrdered(
            guildAudioPlayer,
            event.getOption("url").getAsString(),
            new AudioResultHandler(event));
    }

    private final class AudioResultHandler implements AudioLoadResultHandler {

        /**
         * The interaction event that this result handler is for
         */
        private final SlashCommandInteractionEvent event;

        /**
         * Creates a new {@code AudioResultHandler} for a
         * slash command interaction
         * 
         * @param event The slash command interaction event
         */
        public AudioResultHandler(SlashCommandInteractionEvent event) {
            this.event = event;
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            getGuildAudioPlayer(event.getGuild()).queue(track);
            event.getChannel().sendMessageEmbeds(
                EmbedFactory.createSuccessEmbed(
                    "Audio Added to Queue",
                    "Audio has been added to the audio queue",
                    new Field[] {
                        new Field("Title", track.getInfo().title, false),
                        new Field("Author", track.getInfo().author, false),
                        new Field("Added by", event.getMember().getAsMention(), false)
                    }
                )
            ).queue();
        }

        @Override
        public void loadFailed(FriendlyException e) {
            event.getChannel().sendMessageEmbeds(
                EmbedFactory.createErrorEmbed(
                    "Failed to add Audio",
                    e.getMessage()
                )
            ).queue();
        }

        @Override
        public void noMatches() {
            event.getChannel().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed(
                    "Failed to add Audio",
                    "No match was found"
                )
            ).queue();
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            if (playlist.getTracks().size() < 1) {
                event.getChannel().sendMessageEmbeds(
                    EmbedFactory.createWarningEmbed(
                        "Failed to add Audio",
                        "No match was found"
                    )
                ).queue();
                return;
            }

            AudioTrack track = playlist.getTracks().get(0);

            getGuildAudioPlayer(event.getGuild()).queue(track);
            event.getChannel().sendMessageEmbeds(
                EmbedFactory.createSuccessEmbed(
                    "Audio Added to Queue",
                    "Audio has been added to the audio queue",
                    new Field[] {
                        new Field("Title", track.getInfo().title, false),
                        new Field("Author", track.getInfo().author, false),
                        new Field("Added by", event.getMember().getAsMention(), false)
                    }
                )
            ).queue();
        }
    }
}
