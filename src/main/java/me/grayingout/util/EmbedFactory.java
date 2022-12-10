package me.grayingout.util;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.audit.AuditLogKey;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.GenericChannelEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.role.GenericRoleEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;

/**
 * A class used for making different types
 * of pre-made embeds
 */
public final class EmbedFactory {

    /**
     * An embed for when an integer option is not valid
     * 
     * @param optionName The name of the option
     * @return The build embed
     */
    public static final MessageEmbed createInvalidIntegerOptionEmbed(String optionName) {
        return EmbedFactory.createErrorEmbed("Invalid Argument", "`" + optionName + "` is not a valid integer");
    }

    /**
     * An embed for logging a bulk delete command usage
     * 
     * @param moderator The user that used the command
     * @param channel   The channel the command was used in
     * @param count     The number of messages deleted
     * @return The build embed
     */
    public static final MessageEmbed createBulkDeleteUsageEmbed(Member moderator, GuildMessageChannel channel, int count) {
        MessageEmbed embed = new EmbedBuilder()
            .setColor(Color.RED)
            .setTitle("🧨 **" + count + " Messages Purged in " + channel.getAsMention() + "**")
            .addField("Channel", channel.getAsMention() + " (" + channel.getId() + ")", false)
            .addField("Moderator", moderator.getAsMention() + " (" + moderator.getId() + ")", false)
            .addField("Count", Integer.toString(count), false)
            .build();
        
        return embed;
    }

    /**
     * An embed for when an audio channel command is executed
     * outside an audio channel
     * 
     * @return The build embed
     */
    public static final MessageEmbed createNotExecutedInAudioChannelEmbed() {
        return createWarningEmbed("Invalid Audio Channel", "This command can only be used in an audio channel");
    }

    /**
     * An embed for when the bot is not connected to the same
     * audio channel as the member executing the command
     * 
     * @return The build embed
     */
    public static final MessageEmbed createNotConnectedToSameAudioChannelEmbed() {
        return createWarningEmbed("Invalid Audio Channel", "You must be connected to the same audio channel as me to use this command");
    }

    /**
     * An embed for when the bot is not connected to an audio
     * channel for executing a command
     * 
     * @return The build embed
     */
    public static final MessageEmbed createNotConnectedToAudioChannelEmbed() {
        return createWarningEmbed("Invalid Audio Channel", "I must be connected to an audio channel to use this command");
    }

    /**
     * An embed for logging a role event
     * 
     * @param event The role event
     * @return The built embed
     */
    public static final MessageEmbed createRoleEventLogEmbed(GenericRoleEvent event) {
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.BLUE)
            .setTimestamp(LocalDateTime.now());
        
        /* Add the fields */
        builder.addField("Role", event.getRole().getAsMention(), false);
        builder.addField("Role Id", event.getRole().getId(), false);
        
        /* Get title and add event specific fields */
        StringBuilder titleBuilder = new StringBuilder();
        if (event instanceof RoleCreateEvent) {
            titleBuilder.append(":performing_arts: Role Created");
        } else if (event instanceof RoleDeleteEvent) {
            titleBuilder.append(":x: Role Deleted");
        } else if (event instanceof RoleUpdateNameEvent) {
            titleBuilder.append(":pencil: Role Renamed");
            builder.addField("New Name", ((RoleUpdateNameEvent) event).getNewValue(), false);
            builder.addField("Old Name", ((RoleUpdateNameEvent) event).getOldValue(), false);
        } else if (event instanceof RoleUpdatePermissionsEvent) {
            titleBuilder.append(":scales: Role Permissions Updated");
            builder.addField(
                "New Permissions",
                ((RoleUpdatePermissionsEvent) event).getNewPermissions()
                    .stream()
                    .map(p -> "`"+p.getName()+"`")
                    .collect(Collectors.joining(", ")),
                false
            );
            builder.addField(
                "Old Permissions",
                ((RoleUpdatePermissionsEvent) event).getOldPermissions()
                    .stream()
                    .map(p -> "`"+p.getName()+"`")
                    .collect(Collectors.joining(", ")),
                false
            );
        }
        builder.setTitle(titleBuilder.toString());

        /* Find the user that did the action */
        User user = null;
        if (event instanceof RoleCreateEvent) {
            /* Get the audit logs of type ROLE_CREATE */
            List<AuditLogEntry> entries = event.getGuild()
                .retrieveAuditLogs()
                .limit(10)
                .type(ActionType.ROLE_CREATE)
                .complete()
                .stream()
                .filter(e -> e.getTargetIdLong() == event.getRole().getIdLong())
                .collect(Collectors.toList());
            /* Get the user */
            user = entries.size() == 0 ? null : entries.get(0).getUser();
        } else if (event instanceof RoleDeleteEvent) {
            /* Get the audit logs of type ROLE_DELETE */
            List<AuditLogEntry> entries = event.getGuild()
                .retrieveAuditLogs()
                .limit(10)
                .type(ActionType.ROLE_DELETE)
                .complete()
                .stream()
                .filter(e -> e.getTargetIdLong() == event.getRole().getIdLong())
                .collect(Collectors.toList());
            /* Get the user */
            user = entries.size() == 0 ? null : entries.get(0).getUser();
        } else if (event instanceof RoleUpdateNameEvent) {
            /* Get the audit logs of type ROLE_UPDATE of change type ROLE_NAME */
            List<AuditLogEntry> entries = event.getGuild()
                .retrieveAuditLogs()
                .limit(10)
                .type(ActionType.ROLE_UPDATE)
                .complete()
                .stream()
                .filter(e -> e.getTargetIdLong() == event.getRole().getIdLong())
                .filter(e -> e.getChangeByKey(AuditLogKey.ROLE_NAME) != null)
                .collect(Collectors.toList());
            /* Get the user */
            user = entries.size() == 0 ? null : entries.get(0).getUser();
        } else if (event instanceof RoleUpdatePermissionsEvent) {
            /* Get the audit logs of type ROLE_UPDATE of change type ROLE_PERMISSIONS */
            List<AuditLogEntry> entries = event.getGuild()
                .retrieveAuditLogs()
                .limit(10)
                .type(ActionType.ROLE_UPDATE)
                .complete()
                .stream()
                .filter(e -> e.getTargetIdLong() == event.getRole().getIdLong())
                .filter(e -> e.getChangeByKey(AuditLogKey.ROLE_PERMISSIONS) != null)
                .collect(Collectors.toList());
            /* Get the user */
            user = entries.size() == 0 ? null : entries.get(0).getUser();
        }

        /* Add the user */
        builder.addField("User", user == null ? "<unknown>" : user.getAsMention(), false);

        /* Return the embed */
        return builder.build();
    }

    /**
     * An embed for logging a channel event
     * 
     * @param event The channel event
     * @return The built embed
     */
    public static final MessageEmbed createChannelEventLogEmbed(GenericChannelEvent event) {
        String channelType = event.getChannelType().equals(ChannelType.CATEGORY) ? "Category" : "Channel";

        /* The embed builder */
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.BLUE)
            .setTimestamp(LocalDateTime.now());

        /* Add the fields */
        builder.addField(channelType, event.getChannel().getAsMention(), false);
        builder.addField(channelType + " Id", event.getChannel().getId(), false);

        /* Get the embed title and event specific fields */
        StringBuilder titleBuilder = new StringBuilder();
        if (event instanceof ChannelDeleteEvent) {
            titleBuilder.append(":x: " + channelType + " Deleted");
        } else if (event instanceof ChannelUpdateNameEvent) {
            titleBuilder.append(":pencil: " + channelType + " Renamed");
            builder.addField("New Name", ((ChannelUpdateNameEvent) event).getNewValue(), false);
            builder.addField("Old Name", ((ChannelUpdateNameEvent) event).getOldValue(), false);
        } else if (event instanceof ChannelCreateEvent) {
            titleBuilder.append(":file_folder: " + channelType + " Created");
        }
        builder.setTitle(titleBuilder.toString());

        /* Find the user that did the action */
        User user = null;
        if (event instanceof ChannelDeleteEvent) {
            /* Get the audit logs of type CHANNEL_DELETE */
            List<AuditLogEntry> entries = event.getGuild()
                .retrieveAuditLogs()
                .limit(10)
                .type(ActionType.CHANNEL_DELETE)
                .complete()
                .stream()
                .filter(e -> e.getTargetIdLong() == event.getChannel().getIdLong())
                .collect(Collectors.toList());
            /* Get the user */
            user = entries.size() == 0 ? null : entries.get(0).getUser();
        } else if (event instanceof ChannelUpdateNameEvent) {
            /* Get the audit logs of type CHANNEL_UPDATE of change type CHANNEL_NAME */
            List<AuditLogEntry> entries = event.getGuild()
                .retrieveAuditLogs()
                .limit(10)
                .type(ActionType.CHANNEL_UPDATE)
                .complete()
                .stream()
                .filter(e -> e.getTargetIdLong() == event.getChannel().getIdLong())
                .filter(e -> e.getChangeByKey(AuditLogKey.CHANNEL_NAME) != null)
                .collect(Collectors.toList());
            /* Get the user */
            user = entries.size() == 0 ? null : entries.get(0).getUser();
        } else if (event instanceof ChannelCreateEvent) {
            /* Get the audit logs of type CHANNEL_CREATE */
            List<AuditLogEntry> entries = event.getGuild()
                .retrieveAuditLogs()
                .limit(10)
                .type(ActionType.CHANNEL_CREATE)
                .complete()
                .stream()
                .filter(e -> e.getTargetIdLong() == event.getChannel().getIdLong())
                .collect(Collectors.toList());
            /* Get the user */
            user = entries.size() == 0 ? null : entries.get(0).getUser();
        }

        /* Add the user */
        builder.addField("User", user == null ? "<unknown>" : user.getAsMention(), false);

        /* Build and return */
        return builder.build();
    }

    /**
     * An embed for logging a deleted message
     * 
     * @param message The deleted message
     * @return The built embed, and any embeds on the message
     */
    public static final Collection<MessageEmbed> createDeletedMessageLogEmbed(Message message) {
        List<MessageEmbed> embeds = new ArrayList<>();
        
        /* Build deleted message embed */
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.BLUE)
            .setTitle("✂ **Message Deleted**")
            .addField("Channel", message.getChannel().getAsMention() + " (" + message.getChannel().getId() + ")", false)
            .addField("Author", message.getAuthor().getAsMention() + " (" + message.getAuthor().getId() + ")", false)
            .setFooter(message.getId())
            .setTimestamp(LocalDateTime.now());
        
        /* Add content field if has content */
        if (message.getContentRaw().length() > 0) {
            builder.addField("Content", message.getContentRaw(), false);
        }
        
        /* Add attachments if it has attachments */
        StringBuilder attachments = new StringBuilder();
        for (Attachment attachment : message.getAttachments()) {
            attachments.append(attachment.getProxyUrl() + "\n");
        }
        if (attachments.length() > 0) {
            builder.addField("Attachments", attachments.toString(), false);
        }

        /* Create list of embeds */
        embeds.add(builder.build());
        embeds.addAll(message.getEmbeds());

        return embeds;
    }

    /**
     * An embed for logging a deleted message that wasn't
     * in the {@code MessageCache}
     * 
     * @param channel   The channel the message was deleted from
     * @param messageId The id of the deleted message
     * @return The built embed
     */
    public static final Collection<MessageEmbed> createUnknownDeletedMessageLogEmbed(Channel channel, long messageId) {
        List<MessageEmbed> embeds = new ArrayList<>();

        /* Build deleted message embed */
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.BLUE)
            .setTitle("✂ **Message Deleted in " + channel.getAsMention() + "**")
            .setDescription(":warning: A message was deleted that was not stored in the bot's message cache. Limited data is available.")
            .addField("Channel", channel.getAsMention() + " (" + channel.getId() + ")", false)
            .setFooter(Long.toString(messageId))
            .setTimestamp(LocalDateTime.now());
        
        /* Create list of embeds */
        embeds.add(builder.build());

        return embeds;
    }

    /**
     * An embed used when an exception occurs and the
     * bot cannot proceed any further
     * 
     * @param e       The exception
     * @param message The message shown to the user
     * @returns The built embed
     */
    public static final MessageEmbed createExceptionEmbed(Throwable t, String message) {
        MessageEmbed embed = new EmbedBuilder()
            .setColor(Color.RED)
            .setTitle("**:red_circle: " + t.getClass().getSimpleName() + "**")
            .setDescription(message)
            .setTimestamp(LocalDateTime.now())
            .build();
        
        return embed;
    }

    /**
     * Am embed used when the bot cannot complete the operation
     * with an option for fields
     * 
     * @param title   The title of the error
     * @param message The message shown to the user
     * @param fields  The embed fields to add
     * @return The built embed
     */
    public static final MessageEmbed createErrorEmbed(String title, String message, Field... fields) {
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.RED)
            .setTitle("**:x: " + title + "**")
            .setDescription(message)
            .setTimestamp(LocalDateTime.now());
        
        /* Add fields */
        for (Field field : fields) {
            builder.addField(field);
        }
        
        return builder.build();
    }

    /**
     * Am embed used when the bot cannot complete the operation
     * 
     * @param title   The title of the error
     * @param message The message shown to the user
     * @return The built embed
     */
    public static final MessageEmbed createErrorEmbed(String title, String message) {
        return createErrorEmbed(title, message, new Field[] {});
    }

    /**
     * An embed used when the bot cannot complete the operation
     * with the option for fields
     * 
     * @param title   The title of the warning
     * @param message The message shown to the user
     * @param fields  The embed fields to add
     * @return The built embed
     */
    public static final MessageEmbed createWarningEmbed(String title, String message, Field... fields) {
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.ORANGE)
            .setTitle("**:warning: " + title + "**")
            .setDescription(message)
            .setTimestamp(LocalDateTime.now());
        
        /* Add fields */
        for (Field field : fields) {
            builder.addField(field);
        }

        return builder.build();
    }

    /**
     * An embed used when the bot cannot complete the operation
     * 
     * @param title   The title of the warning
     * @param message The message shown to the user
     * @return The built embed
     */
    public static final MessageEmbed createWarningEmbed(String title, String message) {
        return createWarningEmbed(title, message, new Field[] {});
    }

    /**
     * An embed used when the bot successfully completed an
     * operation with the option for fields
     * 
     * @param title   The title of the success
     * @param message The message shown to the user
     * @param fields  Any embed fields to add
     * @return The built embed
     */
    public static final MessageEmbed createSuccessEmbed(String title, String message, Field... fields) {
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.GREEN.darker())
            .setTitle("**:white_check_mark: " + title + "**")
            .setDescription(message)
            .setTimestamp(LocalDateTime.now());
        
        /* Add fields */
        for (Field field : fields) {
            builder.addField(field);
        }
        
        return builder.build();
    }

    /**
     * An embed used when the bot successfully completed an
     * operation
     * 
     * @param title   The title of the success
     * @param message The message shown to the user
     * @return The built embed
     */
    public static final MessageEmbed createSuccessEmbed(String title, String message) {
        return createSuccessEmbed(title, message, new Field[] {});
    }

    /**
     * An embed used to send a generic message to the user with
     * the option for fields
     * 
     * @param title   The title of the message
     * @param message The message shown to the user
     * @param fields  Any embed fields to add
     * @return The built embed
     */
    public static final MessageEmbed createGenericEmbed(String title, String message, Field... fields) {
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.BLUE)
            .setTitle(title)
            .setDescription(message)
            .setTimestamp(LocalDateTime.now());
        
        /* Add fields */
        for (Field field : fields) {
            builder.addField(field);
        }

        return builder.build();
    }

    /**
     * An embed used to send a generic message to the user
     * 
     * @param title   The title of the message
     * @param message The message shown to the user
     * @return The built embed
     */
    public static final MessageEmbed createGenericEmbed(String title, String message) {
        return createGenericEmbed(title, message, new Field[] {});
    }
}
