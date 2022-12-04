package me.grayingout.bot.events;

import java.util.HashMap;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.EventListener;

/**
 * Caches all messages and does not remove them - including
 * deleted messages. This can be used to get information about deleted
 * messages
 */
public class MessageCache implements EventListener {

    /**
     * The {@code MessageCache} instance
     */
    private static MessageCache instance;

    /**
     * Stores the messages
     */
    private final HashMap<Long, Message> messages;

    /**
     * Creates a new {@code MessageCache}
     */
    private MessageCache() {
        messages = new HashMap<>();
    }

    /**
     * Gets the {@code MessageCache} instance
     * 
     * @return The instance
     */
    public static final MessageCache getInstance() {
        if (instance == null) {
            instance = new MessageCache();
        }

        return instance;
    }

    /**
     * Gets a stored message, or {@code null} if no cached
     * message
     * 
     * @param id The id of the message
     * @return The message, or {@code null}, if not present
     */
    public final Message getMessageByIdLong(long id) {
        return messages.get(id);
    }

    @Override
    public void onEvent(GenericEvent event) {
        /* Add messages */
        if (event instanceof MessageReceivedEvent) {
            Message message = ((MessageReceivedEvent) event).getMessage();
            messages.put(message.getIdLong(), message);
            return;
        }

        /* Update messages */
        if (event instanceof MessageUpdateEvent) {
            Message message = ((MessageUpdateEvent) event).getMessage();
            messages.put(message.getIdLong(), message);
            return;
        }
    }
}
