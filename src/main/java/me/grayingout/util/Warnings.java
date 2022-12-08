package me.grayingout.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.grayingout.database.accessors.DatabaseAccessorManager;
import me.grayingout.database.entities.MemberWarning;
import me.grayingout.database.entities.MemberWarningsListMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

/**
 * Utility methods for the warnings system
 */
public final class Warnings {

    /**
     * The page size of the warnings list
     */
    public static final double WARNINGS_PAGE_SIZE = 5d;

    /**
     * Creates the warning embed sent to a warned user's DMs
     * 
     * @param member The member warned
     * @return The embed
     */
    public static final MessageEmbed createDMWarningEmbed(Member member, Member moderator, String reason) {
        Field[] fields = {
            new Field("Moderator", moderator.getUser().getAsTag() + " (" + moderator.getId() + ")", false),
            new Field("Reason", reason, false)
        };

        MessageEmbed embed = EmbedFactory.createWarningEmbed(
            "You received a warning in " + member.getGuild().getName() + " (" + moderator.getGuild().getId() + ")",
            "",
            fields
        );
        
        return embed;
    }

    /**
     * Creates the warning embed sent as a response to the warn
     * command
     * 
     * @return The embed
     */
    public static final MessageEmbed createWarningSuccessEmbed(Member member, Member moderator, String reason, int warningId) {
        Field[] fields = {
            new Field("Member", String.format(
                "%s (%s)",
                member.getUser().getAsTag(),
                member.getIdLong()), false),
            new Field("Moderator", String.format(
                "%s (%s)",
                moderator.getUser().getAsTag(),
                moderator.getIdLong()), false),
            new Field("Reason", reason, false),
            new Field("Warning Id", Integer.toString(warningId), false)
        };

        return EmbedFactory.createSuccessEmbed(
            "Member Warned",
            member.getUser().getAsTag() + " (" + member.getId() + ") has been warned",
            fields
        );
    }

    /**
     * Creates a warning embed that the user was not found
     * 
     * @return The embed
     */
    public static final MessageEmbed createMemberNotFoundEmbed() {
        return EmbedFactory.createWarningEmbed(
            "Member not Found",
            "The requested member was not found in the guild - maybe they left"
        );
    }

    /**
     * Updates the page shown on a warnings list message
     * 
     * @param warningsListMessageData The data for the warnings message
     * @param page                    The warnings page to show
     */
    public static final void updateWarningsListMessage(
        MemberWarningsListMessage warningsListMessageData,
        int page
    ) {
        Member member = warningsListMessageData.getMember();

        /* Check member still in guild */
        if (member == null) {
            warningsListMessageData.getMessage().editMessage(
                new MessageEditBuilder()
                    .setEmbeds(Warnings.createMemberNotFoundEmbed())
                    .setComponents(new ArrayList<>())
                    .build()    
            ).queue(message -> {
                message.delete().queueAfter(3, TimeUnit.SECONDS);
            });
            return;
        }

        /* Get the member's warnings */
        List<MemberWarning> warnings = DatabaseAccessorManager.getWarningsDatabaseAccessor().getMemberWarnings(member);
        if (warnings == null) {
            return;
        }

        /* Make sure page is within bounds */
        int boundedPage = Math.max(Math.min(getNumberOfPages(warnings.size()), page), 1);

        /* Update page in database */
        DatabaseAccessorManager.getWarningsDatabaseAccessor().updateMemberWarningsListMessagePage(
            warningsListMessageData.getMessage(), boundedPage);
        
        /* Edit message */
        warningsListMessageData.getMessage().editMessage(
            new MessageEditBuilder()
                .setEmbeds(createWarningsPageEmbed(
                    member,
                    warnings,
                    boundedPage
                ))
                .setActionRow(getNavigationButtons(boundedPage, warnings.size()))
                .build()
        ).queue();
    }

    /**
     * Create the navigation buttons based on the current page
     * and number of warnings
     * 
     * @param currentPage      The current page
     * @param numberOfWarnings The number of warnings
     * @return The list of buttons
     */
    public static final List<ItemComponent> getNavigationButtons(int currentPage, int numberOfWarnings) {
        Button prevPageButton = Button.primary("warnings_list_prev_page", "Prev Page");
        Button nextPageButton = Button.primary("warnings_list_next_page", "Next Page");
        Button refreshButton = Button.secondary("warnings_list_refresh_page", "Refresh");

        /* Check if on first page */
        if (currentPage <= 1) {
            prevPageButton = prevPageButton.asDisabled();
        }

        /* Check if on last page */
        if (currentPage >= (int) Math.ceil(numberOfWarnings / WARNINGS_PAGE_SIZE)) {
            nextPageButton = nextPageButton.asDisabled();
        }

        return Arrays.asList(prevPageButton, nextPageButton, refreshButton);
    }

    /**
     * Creates an embed for a page of warnings
     * 
     * @param member    The warned member
     * @param warnings  The warnings
     * @param page      The current page
     * @return The built embed
     */
    public static final MessageEmbed createWarningsPageEmbed(Member member, List<MemberWarning> warnings, int page) {
        /* Get indexes of the start and end of the page */
        int pageStartIndex = getPageStartIndex(page, warnings.size());
        int pageEndIndex = getPageEndIndex(page, warnings.size());
        
        ArrayList<Field> warningFields = new ArrayList<>();
        
        /* Add each field from the current page */
        for (MemberWarning warning : warnings.subList(pageStartIndex, pageEndIndex)) {
            warningFields.add(new Field(
                String.format(
                    ":pen_ballpoint: Warned by **%s** (%s)",
                    warning.getModerator(member.getGuild()).getUser().getAsTag(),
                    warning.getModeratorId()),
                String.format(
                    "**Reason:** %s\n**Warned on:** %s\n[%s]",
                    warning.getReason(),
                    warning.getReceivedAt(),
                    warning.getWarningId()),
                false
            ));
        }

        return EmbedFactory.createGenericEmbed(
            ":clipboard: Warnings for **" + member.getUser().getAsTag() + "**",
            String.format(
                "The user has a total of **%s** warnings\n\nShowing page **%s** of **%s**",
                warnings.size(),
                page,
                getNumberOfPages(warnings.size())
            ),
            warningFields.toArray(new Field[] {})
        );
    }

    /**
     * Calculates the number of pages possible in a list
     * of warnings
     * 
     * @param numberOfWarnings The number of warnings
     * @return The number of possible pages
     */
    public static final int getNumberOfPages(int numberOfWarnings) {
        /* Check if not warning */
        if (numberOfWarnings == 0) {
            return 1;
        }

        /* Calculate page count */
        return (int) Math.ceil(numberOfWarnings / WARNINGS_PAGE_SIZE);
    }

    /**
     * Calculates the start index of the warnings array for the
     * provided page
     * 
     * @param page             The page
     * @param numberOfWarnings The number of warnings
     * @return The start index
     */
    public static final int getPageStartIndex(int page, int numberOfWarnings) {
        /* Check if not warnings */
        if (numberOfWarnings == 0) {
            return 0;
        }

        /* Calculate index */
        int startIndex = (int) ((page - 1) * WARNINGS_PAGE_SIZE);

        /* Check if outside bounds */
        if (startIndex >= numberOfWarnings) {
            startIndex = numberOfWarnings - 1;
        }

        /* Return index */
        return startIndex;
    }

    /**
     * Calculates the end index of the warnings array for the
     * provided page
     * 
     * @param page             The page
     * @param numberOfWarnings The number of warnings
     * @return The end index
     */
    public static final int getPageEndIndex(int page, int numberOfWarnings) {
        /* Get the start index */
        int startIndex = getPageStartIndex(page, numberOfWarnings);

        /* Check if enough warnings after start index for page */
        if (numberOfWarnings >= startIndex + WARNINGS_PAGE_SIZE) {
            return (int) (startIndex + WARNINGS_PAGE_SIZE);
        }

        /* Not enough after start index for page */
        return numberOfWarnings;
    }
}
