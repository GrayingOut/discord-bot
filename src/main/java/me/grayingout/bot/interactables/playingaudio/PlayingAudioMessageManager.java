package me.grayingout.bot.interactables.playingaudio;

import java.util.HashMap;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Manages the playing audio messages to make them interactable
 */
public final class PlayingAudioMessageManager extends ListenerAdapter {

    /**
     * Stores the {@code AudioQueueMessage}s against their message long id
     */
    private static final HashMap<Long, PlayingAudioMessage> messages = new HashMap<>();

    /**
     * Registers an {@code AudioQueueMessage} on the manager
     * 
     * @param playingAudioMessage The {@code AudioQueueMessage}
     */
    public static final void register(PlayingAudioMessage playingAudioMessage) {
        messages.put(playingAudioMessage.getMessage().getIdLong(), playingAudioMessage);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getButton().getId()) {
            case "playing_audio_refresh": {
                event.deferEdit().queue();
                if (messages.get(event.getMessageIdLong()) == null) break;

                messages.get(event.getMessageIdLong()).refresh();
            }
        }
    }
}
