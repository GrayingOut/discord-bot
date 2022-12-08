package me.grayingout.bot.listeners;

import me.grayingout.database.entities.GuildWelcomeMessage;
import me.grayingout.util.EmbedFactory;
import me.grayingout.util.WelcomeMessage;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Listeners for the welcome messages system
 */
public final class WelcomeMessageListeners extends ListenerAdapter {
    
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        GuildWelcomeMessage welcomeMessage = GuildWelcomeMessage.getGuildWelcomeMessage(event.getGuild());
        
        /* No welcome message channel set up */
        if (welcomeMessage.getWelcomeChannel() == null) {
            return;
        }

        String message = WelcomeMessage.formatWelcomeMessage(welcomeMessage, event.getMember());

        /* Create and send the welcome message */
        welcomeMessage.getWelcomeChannel().sendMessageEmbeds(
            EmbedFactory.createGenericEmbed(
                "Welcome",
                message
                )
        ).queue();
    }
}
