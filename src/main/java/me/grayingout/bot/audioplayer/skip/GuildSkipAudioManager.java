package me.grayingout.bot.audioplayer.skip;

import java.util.HashMap;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Manages the {@code GuildSkipAudio}s
 */
public final class GuildSkipAudioManager extends ListenerAdapter {
    
    /**
     * The {@code GuildSkipAudioManager} instance
     */
    private static GuildSkipAudioManager INSTANCE;

    /**
     * Holds the {@code GuildSkipAudio} instances
     */
    private final HashMap<Long, GuildSkipAudio> guildSkipAudios;

    /**
     * Creates a new {@code GuildSkipAudioManager}
     */
    private GuildSkipAudioManager() {
        guildSkipAudios = new HashMap<>();
    }

    /**
     * Gets the {@code GuildSkipAudioManager} instance
     * @return
     */
    public static final GuildSkipAudioManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GuildSkipAudioManager();
        }

        return INSTANCE;
    }

    /**
     * Gets a guild's {@code GuildSkipAudio} instance
     * @param guild
     * @return
     */
    public final GuildSkipAudio getGuildSkipAudio(Guild guild) {
        if (guildSkipAudios.get(guild.getIdLong()) == null) {
            guildSkipAudios.put(guild.getIdLong(), new GuildSkipAudio(guild));
        }
        return guildSkipAudios.get(guild.getIdLong());
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        GuildVoiceState selfVoiceState = event.getGuild().getSelfMember().getVoiceState();

        /* Check if joined audio channel */
        if (event.getChannelJoined() != null) {
            /* Check if member is the bot */
            if (event.getMember().getIdLong() == event.getGuild().getSelfMember().getIdLong()) {
                getGuildSkipAudio(event.getGuild()).botJoinedChannel(selfVoiceState.getChannel());
                return;
            }

            /* Check bot is in a channel */
            if (selfVoiceState.getChannel() == null) {
                return;
            }
            
            /* Check if joined bot's channel */
            if (event.getChannelJoined().getIdLong() == selfVoiceState.getChannel().getIdLong()) {
                getGuildSkipAudio(event.getGuild()).memberJoined();
            }
            return;
        }

        /* Check if left audio channel */
        if (event.getChannelLeft() != null) {
            /* Check if member is the bot */
            if (event.getMember().getIdLong() == event.getGuild().getSelfMember().getIdLong()) {
                getGuildSkipAudio(event.getGuild()).botLeftChannel();
                return;
            }

            /* Check bot is in a channel */
            if (selfVoiceState.getChannel() == null) {
                return;
            }

            /* Check if left bot's channel */
            if (event.getChannelLeft().getIdLong() == selfVoiceState.getChannel().getIdLong()) {
                getGuildSkipAudio(event.getGuild()).memberLeft();
            }
        }
    }
}
