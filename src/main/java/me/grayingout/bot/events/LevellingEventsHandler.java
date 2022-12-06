package me.grayingout.bot.events;

import me.grayingout.database.accessor.DatabaseAccessorManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Listeners for the levelling system
 */
public class LevellingEventsHandler extends ListenerAdapter {
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        /* Ignore own messages */
        if (event.getAuthor().getIdLong() == event.getJDA().getSelfUser().getIdLong()) {
            return;
        }

        /* Ignore bot messages */
        if (event.getAuthor().isBot()) {
            return;
        }

        /* Ignore non-guild messages */
        if (!event.isFromGuild()) {
            return;
        }

        /* Grant experience for the message */
        int newLevel = DatabaseAccessorManager
            .getLevellingDatabaseAccessor()
            .addExperienceToGuildMember(event.getMember(), 1);
        
        if (newLevel != -1) {
            /* New level! */
            event.getChannel().sendMessage(
                ":confetti_ball: Congratulations " + event.getMember().getAsMention() + ". You advanced to level " + newLevel + "!"
            ).queue();
        }
    }
}
