package me.grayingout.bot.interactables.audioqueue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import me.grayingout.bot.audioplayer.GuildAudioPlayerManager;
import me.grayingout.util.Audio;
import me.grayingout.util.Paging;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

/**
 * Wrapper for an audio queue message
 */
public final class AudioQueueMessage {

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

        int boundedPage = Paging.boundPage(page, Audio.QUEUE_PAGE_SIZE, queue.length);
        this.page = boundedPage;

        MessageEditData editData = new MessageEditBuilder()
            .setEmbeds(Audio.createAudioQueueMessageEmbed(page, queue))
            .setActionRow(Audio.getActionRowButtons(boundedPage, queue.length))
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
}
