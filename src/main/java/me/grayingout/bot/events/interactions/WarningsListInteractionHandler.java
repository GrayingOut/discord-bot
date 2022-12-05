package me.grayingout.bot.events.interactions;

import me.grayingout.database.WarningsDatabase;
import me.grayingout.database.objects.MemberWarningsListMessage;
import me.grayingout.util.Warnings;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Handles the user interacting with a warnings list
 */
public final class WarningsListInteractionHandler extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getButton().getId()) {
            case "warnings_list_refresh_page": {
                event.deferEdit().queue();

                /* Get the warnings list message data */
                MemberWarningsListMessage mwlm = WarningsDatabase.getMemberWarningsListMessage(event.getMessage());
                
                Warnings.updateWarningsListMessage(mwlm, mwlm.getCurrentPage());
                break;
            }

            case "warnings_list_next_page": {
                event.deferEdit().queue();

                /* Get the warnings list message data */
                MemberWarningsListMessage mwlm = WarningsDatabase.getMemberWarningsListMessage(event.getMessage());
    
                Warnings.updateWarningsListMessage(mwlm, mwlm.getCurrentPage() + 1);
                break;
            }

            case "warnings_list_prev_page": {
                event.deferEdit().queue();

                /* Get the warnings list message data */
                MemberWarningsListMessage mwlm = WarningsDatabase.getMemberWarningsListMessage(event.getMessage());
    
                Warnings.updateWarningsListMessage(mwlm, mwlm.getCurrentPage() - 1);
                break;
            }
        }
    }
}
