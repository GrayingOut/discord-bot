package me.grayingout.bot.events;

import me.grayingout.database.guildconfig.ConfigDatabase;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * Detects a message being deleted and logs
 * it to a logging channel
 */
public class DeletedMessageLogger extends ListenerAdapter {

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        long loggingChannelId = ConfigDatabase.getLoggingChannelId(event.getGuild());

        /* Check logging channel exists */
        if (loggingChannelId == -1) {
            return;
        }

        /* Get the channel */
        GuildMessageChannel channel = event.getGuild().getChannelById(GuildMessageChannel.class, loggingChannelId);
        if (channel == null) {
            return;
        }

        /* Get the deleted message from the cache */
        Message message = MessageCache.getInstance().getMessageByIdLong(event.getMessageIdLong());

        /* Ignore if deleted in logging channel (can only check if message was in cache) */
        if (event.getChannel().getIdLong() == loggingChannelId) {
            return;
        }
        
        /* Create the log message */
        MessageCreateData logMessage = null;

        if (message != null) {
            logMessage = new MessageCreateBuilder()
                .setContent("**MESSAGE DELETED**")
                .setEmbeds(EmbedFactory.createDeletedMessageLogEmbed(message))
                .build();
        } else {
            logMessage = new MessageCreateBuilder()
                .setContent("**MESSAGE DELETED**")
                .setEmbeds(EmbedFactory.createUnknownDeletedMessageLogEmbed(event.getChannel(), event.getMessageIdLong()))
                .build();
        }
        
        channel.sendMessage(logMessage).queue();
    }
}
