package me.grayingout.util;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import me.grayingout.bot.audioplayer.GuildAudioPlayerManager;
import me.grayingout.bot.audioplayer.handler.AudioLoadResult;
import me.grayingout.bot.audioplayer.handler.AudioLoadResultType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * Utility methods for the audio system
 */
public final class Audio {

    /**
     * The page size of the warnings list
     */
    public static final int QUEUE_PAGE_SIZE = 5;

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

    /**
     * Creates an new message to display the audio queue
     * 
     * @param guild The guild the message is for
     * @return The created message
     */
    public static final MessageCreateData createAudioQueueMessageMessage(Guild guild) {
        AudioTrack[] queue = GuildAudioPlayerManager.getInstance().getGuildAudioPlayer(guild).getAudioTrackQueue();

        MessageCreateData createData = new MessageCreateBuilder()
            .addEmbeds(createAudioQueueMessageEmbed(1, queue))
            .addActionRow(getActionRowButtons(1, queue.length))
            .build();

        return createData;
    }

    /**
     * Creates the embed that shows the list of tracks on a page
     * 
     * @param page        The page to show
     * @param queueTracks The audio tracks queue
     * @return The built embed
     */
    public static final MessageEmbed createAudioQueueMessageEmbed(int page, AudioTrack[] queueTracks) {
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.BLUE)
            .setTitle("Audio Queue")
            .setDescription(
                queueTracks.length == 0
                ? "There are currently no tracks in the queue. You can add tracks using `/play` or `/search`"
                : "There is currently " + queueTracks.length + " track" + (queueTracks.length == 1 ? "" : "s") + " in the queue");
        
        /* Get start and end index */
        int startIndex = Paging.getPageStartIndex(page, QUEUE_PAGE_SIZE, queueTracks.length);
        int endIndex = Paging.getPageEndIndex(page, QUEUE_PAGE_SIZE, queueTracks.length);
        
        /* Display the page tracks */
        for (int i = startIndex; i < endIndex; i++) {
            AudioTrack track = queueTracks[i];
            builder.addField(
                (i+1) + ". " + track.getInfo().title,
                "Author: " + track.getInfo().author + "\nLength: `" + Audio.formatAudioTrackTime(track.getDuration()/1000) + "`",
                false
            );
        }
        
        return builder.build();
    }

    /**
     * Creates the action row buttons for the current page
     * 
     * @param page        The page showing
     * @param queueLength The length of the queue
     * @return The list of action row buttons
     */
    public static final List<ItemComponent> getActionRowButtons(int page, int queueLength) {
        Button prevButton = Button.primary("audio_queue_prev_page", "Prev Page");
        Button nextButton = Button.primary("audio_queue_next_page", "Next Page");
        Button refreshButton = Button.secondary("audio_queue_refresh", "Refresh");
        Button clearButton = Button.danger("audio_queue_clear", "Clear");

        /* Check if on first page */
        if (page <= 1) {
            prevButton = prevButton.asDisabled();
        }

        /* Check if on last page */
        if (page >= Paging.getNumberOfPages(QUEUE_PAGE_SIZE, queueLength)) {
            nextButton = nextButton.asDisabled();
        }

        return Arrays.asList(prevButton, nextButton, refreshButton, clearButton);
    }
}
