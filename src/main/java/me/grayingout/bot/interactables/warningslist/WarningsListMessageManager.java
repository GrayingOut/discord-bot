package me.grayingout.bot.interactables.warningslist;

import java.util.HashMap;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Manages the warnings list messages to make them interactable
 */
public final class WarningsListMessageManager extends ListenerAdapter {

    /**
     * Stores the {@code AudioQueueMessage}s against their message long id
     */
    private static final HashMap<Long, WarningsListMessage> messages = new HashMap<>();

    /**
     * Registers an {@code WarningsListMessage} on the manager
     * 
     * @param playingAudioMessage The {@code AudioQueueMessage}
     */
    public static final void register(WarningsListMessage warningsListMessage) {
        messages.put(warningsListMessage.getMessage().getIdLong(), warningsListMessage);
    }
    
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getButton().getId()) {
            case "warnings_list_refresh_page": {
                event.deferEdit().queue();
                if (messages.get(event.getMessageIdLong()) == null) break;
                
                messages.get(event.getMessageIdLong()).refresh();
                break;
            }

            case "warnings_list_next_page": {
                event.deferEdit().queue();
                if (messages.get(event.getMessageIdLong()) == null) break;
    
                messages.get(event.getMessageIdLong()).nextPage();
                break;
            }

            case "warnings_list_prev_page": {
                event.deferEdit().queue();
                if (messages.get(event.getMessageIdLong()) == null) break;
    
                messages.get(event.getMessageIdLong()).prevPage();
                break;
            }
        }
    }
}
