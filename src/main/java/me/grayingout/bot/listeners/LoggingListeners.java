package me.grayingout.bot.listeners;

import me.grayingout.database.entities.GuildLoggingChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Listeners for the logging system
 */
public class LoggingListeners extends ListenerAdapter {

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        /* Log deleted messages */
        GuildLoggingChannel.getGuildLoggingChannel(event.getGuild()).logDeletedMessage(event);
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        /* Log channel create */
        GuildLoggingChannel.getGuildLoggingChannel(event.getGuild()).logChannelCreate(event);
    }

    @Override
    public void onChannelDelete(ChannelDeleteEvent event) {
        /* Log channel delete */
        GuildLoggingChannel.getGuildLoggingChannel(event.getGuild()).logChannelDelete(event);
    }

    @Override
    public void onChannelUpdateName(ChannelUpdateNameEvent event) {
        /* Log channel name change */
        GuildLoggingChannel.getGuildLoggingChannel(event.getGuild()).logChannelNameChanges(event);
    }
}
