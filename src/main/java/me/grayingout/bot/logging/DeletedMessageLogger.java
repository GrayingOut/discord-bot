package me.grayingout.bot.logging;

import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Detects a message being deleted and logs it
 */
public class DeletedMessageLogger extends ListenerAdapter {

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        /* Log the message */
        BotLoggingChannel.getGuildBotLoggingChannel(event.getGuild()).logDeletedMessage(event);
    }
}
