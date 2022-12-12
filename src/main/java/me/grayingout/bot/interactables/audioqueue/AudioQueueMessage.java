package me.grayingout.bot.interactables.audioqueue;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import me.grayingout.bot.audioplayer.GuildAudioPlayerManager;
import me.grayingout.util.Audio;
import me.grayingout.util.Paging;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

/**
 * Wrapper for an audio queue message
 */
public final class AudioQueueMessage {

    /**
     * The page size of the warnings list
     */
    public static final int QUEUE_PAGE_SIZE = 5;

    /**
     * The audio queue message
     */
    private final Message message;

    /**
     * The current page the queue is on
     */
    private int page;

    /**
     * Creates a new {@code AudioQueueMessage}
     * 
     * @param event The slash command
     */
    public AudioQueueMessage(Message message) {
        this.message = message;
        page = 1;
    }

    /**
     * Gets the message associated with this {@code AudioQueueMessage}
     * 
     * @return The message
     */
    public final Message getMessage() {
        return message;
    }

    /**
     * Gets the page the message is currently showing
     * 
     * @return
     */
    public final int getPage() {
        return page;
    }

    /**
     * Shows the next page
     */
    public final void nextPage() {
        showPage(page + 1);
    }

    /**
     * Shows the previous page
     */
    public final void prevPage() {
        showPage(page - 1);
    }
    
    /**
     * Update the message to show a specific page
     * 
     * @param page The page to show
     */
    public final void showPage(int page) {
        AudioTrack[] queue = GuildAudioPlayerManager.getInstance().getGuildAudioPlayer(message.getGuild()).getAudioTrackQueue();

        int boundedPage = Paging.boundPage(page, QUEUE_PAGE_SIZE, queue.length);
        this.page = boundedPage;

        MessageEditData editData = new MessageEditBuilder()
            .setEmbeds(createAudioQueueMessageEmbed(page, queue))
            .setActionRow(getActionRowButtons(boundedPage, queue.length))
            .build();

        /* Edit message */
        message.editMessage(editData).queue();
    }

    /**
     * Refresh the message
     */
    public final void refresh() {
        showPage(page);
    }

    /**
     * Creates a new {@code MessageCreateData} to display the audio queue
     * 
     * @param guild The guild the message is for
     * @return The {@code MessageCreateData}
     */
    public static final MessageCreateData createAudioQueueMessageData(Guild guild) {
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
    private static final MessageEmbed createAudioQueueMessageEmbed(int page, AudioTrack[] queueTracks) {
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
    private static final List<ItemComponent> getActionRowButtons(int page, int queueLength) {
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
