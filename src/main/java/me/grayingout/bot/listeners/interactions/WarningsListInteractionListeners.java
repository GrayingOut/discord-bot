package me.grayingout.bot.listeners.interactions;

import me.grayingout.database.accessors.DatabaseAccessorManager;
import me.grayingout.database.entities.MemberWarningsListMessage;
import me.grayingout.util.Warnings;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Handles the user interacting with a warnings list
 */
public final class WarningsListInteractionListeners extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getButton().getId()) {
            case "warnings_list_refresh_page": {
                event.deferEdit().queue();

                /* Get the warnings list message data */
                MemberWarningsListMessage mwlm = DatabaseAccessorManager
                    .getWarningsDatabaseAccessor()
                    .getMemberWarningsListMessage(event.getMessage());
                
                Warnings.updateWarningsListMessage(mwlm, mwlm.getCurrentPage());
                break;
            }

            case "warnings_list_next_page": {
                event.deferEdit().queue();

                /* Get the warnings list message data */
                MemberWarningsListMessage mwlm = DatabaseAccessorManager
                    .getWarningsDatabaseAccessor()
                    .getMemberWarningsListMessage(event.getMessage());
    
                Warnings.updateWarningsListMessage(mwlm, mwlm.getCurrentPage() + 1);
                break;
            }

            case "warnings_list_prev_page": {
                event.deferEdit().queue();

                /* Get the warnings list message data */
                MemberWarningsListMessage mwlm = DatabaseAccessorManager
                    .getWarningsDatabaseAccessor()
                    .getMemberWarningsListMessage(event.getMessage());
    
                Warnings.updateWarningsListMessage(mwlm, mwlm.getCurrentPage() - 1);
                break;
            }
        }
    }
}