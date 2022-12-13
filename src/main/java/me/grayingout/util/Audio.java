package me.grayingout.util;

import java.util.EnumSet;

import me.grayingout.bot.audioplayer.handler.AudioLoadResult;
import me.grayingout.bot.audioplayer.handler.AudioLoadResultType;
import me.grayingout.database.accessors.DatabaseAccessorManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Utility methods for the audio system
 */
public final class Audio {

    /**
     * Returns whether a member is a DJ (they can use
     * DJ commands, which are restricted commands)
     * 
     * @param member The member to check
     * @return If the member is a DJ
     */
    public static final boolean isMemberAValidDJ(Member member) {
        /* Check member's permissions first, and owner status */
        EnumSet<Permission> permissions = member.getPermissions();
        if (permissions.contains(Permission.ADMINISTRATOR) || permissions.contains(Permission.MANAGE_SERVER) || member.isOwner()) {
            return true;
        }

        /* Check DJ role */
        Role djRole = DatabaseAccessorManager.getConfigurationDatabaseAccessor()
            .getGuildDJRole(member.getGuild());
        if (member.getRoles().contains(djRole)) {
            return true;
        }

        return false;
    }

    /**
     * Formats the duration/position of an {@code AudioTrack} into
     * HH:mm:ss
     * 
     * @param time The time in seconds
     * @return The formatted time
     */
    public static final String formatAudioTrackTime(long timeSeconds) {
        return String.format("%02d:%02d:%02d", (int)(timeSeconds/3600), (int)((timeSeconds%3600)/60), (int)(timeSeconds%60));
    }

    /**
     * Handles the audio load result for a slash command
     * 
     * @param event  The slash command event
     * @param result The audio load result
     */
    public static final void handleAudioLoadResult(SlashCommandInteractionEvent event, AudioLoadResult loadResult) {
        /* Track queued */
        if (loadResult.getResultType().equals(AudioLoadResultType.TRACK_LOADED)) {
            event.getHook().sendMessageEmbeds(EmbedFactory.createSuccessEmbed(
                "Audio has been Queued",
                "The requested audio has been added to the queue",
                new Field[] {
                    new Field("Title", loadResult.getLoadedAudioTrack().getInfo().title, false),
                    new Field("Author", loadResult.getLoadedAudioTrack().getInfo().author, false)
                }
            )).queue();
            return;
        }

        /* Already in the queue */
        if (loadResult.getResultType().equals(AudioLoadResultType.ALREADY_ADDED)) {
            event.getHook().sendMessageEmbeds(EmbedFactory.createWarningEmbed(
                "Audio Already Added",
                "The requested audio is already in the queue"
            )).queue();
            return;
        }

        /* No match */
        if (loadResult.getResultType().equals(AudioLoadResultType.NO_MATCH)) {
            event.getHook().sendMessageEmbeds(EmbedFactory.createWarningEmbed(
                "Audio Not Found",
                "The requested audio was not found"
            )).queue();
            return;
        }

        /* Error */
        event.getHook().sendMessageEmbeds(EmbedFactory.createWarningEmbed(
            "An Error Occurred",
            "An error occurred queueing the requested audio. Please try again."
        )).queue();
    }

    /**
     * Checks the state of the environment the audio slash command
     * was executed in is valid. If not it will send an error
     * response.
     * 
     * @param event             The slash command event
     * @param checkBotInChannel Should it also check if the bot is in a channel
     * @return If it is a valid execution environment
     */
    public static final boolean checkValidCommandExecutionState(SlashCommandInteractionEvent event, boolean checkBotInChannel) {
        /* Check channel is an audio channel */
        if (!isAudioChannel(event.getChannel())) {
            event.getHook().sendMessageEmbeds(EmbedFactory.createNotExecutedInAudioChannelEmbed()).queue();
            return false;
        }
        
        /* Check user is in an audio channel */
        if (!isMemberConnectedToAudioChannel(event.getMember())) {
            event.getHook().sendMessageEmbeds(EmbedFactory.createNotConnectedToSameAudioChannelEmbed()).queue();
            return false;
        }

        if (checkBotInChannel) {
            /* Check bot is in an audio channel */
            if (!isBotInAudioChannel(event.getGuild())) {
                event.getHook().sendMessageEmbeds(EmbedFactory.createNotConnectedToAudioChannelEmbed()).queue();
                return false;
            }
    
            /* Check member in the same audio channel as the bot */
            if (!isBotInSameAudioChannelAsMember(event.getMember())) {
                event.getHook().sendMessageEmbeds(EmbedFactory.createNotConnectedToSameAudioChannelEmbed()).queue();
                return false;
            }
        }

        return true;
    }
    
    /**
     * Returns if a channel is an audio channel
     * 
     * @param channel The channel
     * @return If it is an audio channel
     */
    public static final boolean isAudioChannel(Channel channel) {
        return channel instanceof AudioChannel;
    }

    /**
     * Returns if a guild member is connected to a guild channel
     * 
     * @param member The member
     * @return If they are connected to an audio channel
     */
    public static final boolean isMemberConnectedToAudioChannel(Member member) {
        return member.getVoiceState().getChannel() != null;
    }

    /**
     * Join a member's audio channel if they are in one
     * 
     * @param member The member
     * @return {@code true} if the member was in an audio channel, else {@code false}
     */
    public static final boolean connectToMembersAudioChannel(Member member) {
        if (member.getVoiceState().getChannel() == null) {
            return false;
        }

        member.getGuild().getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
        return true;
    }

    /**
     * Returns if the bot is connected to a audio channel
     * in a guild
     * 
     * @param guild The guild
     * @return If connected to an audio channel
     */
    public static final boolean isBotInAudioChannel(Guild guild) {
        return guild.getSelfMember().getVoiceState().getChannel() != null;
    }

    /**
     * Returns if the bot is in the same audio channel as
     * a guild member
     * 
     * @param member The member
     * @return If the bot is connected to their audio channel
     */
    public static final boolean isBotInSameAudioChannelAsMember(Member member) {
        if (!isMemberConnectedToAudioChannel(member)) {
            return false;
        }

        if (!isBotInAudioChannel(member.getGuild())) {
            return false;
        }

        if (member.getVoiceState().getChannel().getIdLong() == member.getGuild().getSelfMember().getVoiceState().getChannel().getIdLong()) {
            return true;
        }

        return false;
    }
}
