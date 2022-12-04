package me.grayingout.bot.events;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.grayingout.database.guildconfig.ConfigDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
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
                .setEmbeds(createDeletedMessageLogEmbed(message))
                .build();
        } else {
            logMessage = new MessageCreateBuilder()
                .setContent("**MESSAGE DELETED**")
                .setEmbeds(createUnknownDeletedMessageLogEmbed(event.getChannel(), event.getMessageIdLong()))
                .build();
        }
        
        channel.sendMessage(logMessage).queue();
    }

    /**
     * Creates an embed for logging a deleted message
     * 
     * @param message The deleted message
     * @return The built embed, and any embeds on the message
     */
    private final Collection<MessageEmbed> createDeletedMessageLogEmbed(Message message) {
        List<MessageEmbed> embeds = new ArrayList<>();
        
        /* Build deleted message embed */
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.RED)
            .setTitle("✂ **Deleted Message in " + message.getChannel().getAsMention() + "**")
            .addField("Channel", message.getChannel().getAsMention() + " (" + message.getChannel().getId() + ")", false)
            .addField("Author", message.getAuthor().getAsMention() + " (" + message.getAuthor().getId() + ")", false)
            .addField("Content", message.getContentRaw(), false)
            .setFooter(message.getId())
            .setTimestamp(LocalDateTime.now());
        
        /* Create list of embeds */
        embeds.add(builder.build());
        embeds.addAll(message.getEmbeds());

        return embeds;
    }

    /**
     * Creates an embed for logging a deleted message that wasn't
     * in the {@code MessageCache}
     * 
     * @param channel   The channel the message was deleted from
     * @param messageId The id of the deleted message
     * @return The built embed
     */
    private final Collection<MessageEmbed> createUnknownDeletedMessageLogEmbed(Channel channel, long messageId) {
        List<MessageEmbed> embeds = new ArrayList<>();

        /* Build deleted message embed */
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.RED)
            .setTitle("✂ **Deleted Message in " + channel.getAsMention() + "**")
            .setDescription(":warning: A message was deleted that was not stored in the bot's message cache. Limited data is available.")
            .addField("Channel", channel.getAsMention() + " (" + channel.getId() + ")", false)
            .setFooter(Long.toString(messageId))
            .setTimestamp(LocalDateTime.now());
        
        /* Create list of embeds */
        embeds.add(builder.build());

        return embeds;
    }
}
