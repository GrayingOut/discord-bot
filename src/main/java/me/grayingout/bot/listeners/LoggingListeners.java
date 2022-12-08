package me.grayingout.bot.listeners;

import me.grayingout.database.entities.GuildLoggingChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Listeners for the logging system
 */
public class LoggingListeners extends ListenerAdapter {

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        /* Log deleted messages */
        GuildLoggingChannel.getGuildLoggingChannel(event.getGuild()).logMessageDelete(event);
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
        GuildLoggingChannel.getGuildLoggingChannel(event.getGuild()).logChannelNameUpdate(event);
    }

    @Override
    public void onRoleCreate(RoleCreateEvent event) {
        /* Log role create */
        GuildLoggingChannel.getGuildLoggingChannel(event.getGuild()).logRoleCreate(event);
    }

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {
        /* Log role delete */
        GuildLoggingChannel.getGuildLoggingChannel(event.getGuild()).logRoleDelete(event);
    }

    @Override
    public void onRoleUpdateName(RoleUpdateNameEvent event) {
        /* Log role name update */
        GuildLoggingChannel.getGuildLoggingChannel(event.getGuild()).logRoleNameUpdate(event);
    }

    @Override
    public void onRoleUpdatePermissions(RoleUpdatePermissionsEvent event) {
        /* Log role permissions update */
        GuildLoggingChannel.getGuildLoggingChannel(event.getGuild()).logRolePermissionsUpdate(event);
    }
}
