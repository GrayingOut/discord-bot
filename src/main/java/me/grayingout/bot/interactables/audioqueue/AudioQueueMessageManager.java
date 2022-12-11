package me.grayingout.bot.interactables.audioqueue;

import java.util.HashMap;

import me.grayingout.bot.audioplayer.GuildAudioPlayerManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Manages the messages of audio queues to make them
 * interactable
 */
public final class AudioQueueMessageManager extends ListenerAdapter {

    /**
     * Stores the {@code AudioQueueMessage}s against their message long id
     */
    private static final HashMap<Long, AudioQueueMessage> messages = new HashMap<>();

    /**
     * Registers an {@code AudioQueueMessage} on the manager
     * 
     * @param audioQueueMessage The {@code AudioQueueMessage}
     */
    public static final void register(AudioQueueMessage audioQueueMessage) {
        messages.put(audioQueueMessage.getMessage().getIdLong(), audioQueueMessage);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getButton().getId()) {
            case "audio_queue_next_page": {
                event.deferEdit().queue();
                if (messages.get(event.getMessageIdLong()) == null) break;
                
                messages.get(event.getMessageIdLong()).nextPage();
                break;
            }
            case "audio_queue_prev_page": {
                event.deferEdit().queue();
                if (messages.get(event.getMessageIdLong()) == null) break;
                
                messages.get(event.getMessageIdLong()).prevPage();
                break;
            }
            case "audio_queue_refresh": {
                event.deferEdit().queue();
                if (messages.get(event.getMessageIdLong()) == null) break;
                
                messages.get(event.getMessageIdLong()).refresh();
                break;
            }
            case "audio_queue_clear": {
                event.deferEdit().queue();
                if (messages.get(event.getMessageIdLong()) == null) break;

                GuildAudioPlayerManager.getInstance().getGuildAudioPlayer(event.getGuild()).clearQueue();
                messages.get(event.getMessageIdLong()).refresh();
                break;
            }
        }
    }
}
