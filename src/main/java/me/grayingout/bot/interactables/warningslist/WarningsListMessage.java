package me.grayingout.bot.interactables.warningslist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.grayingout.database.accessors.DatabaseAccessorManager;
import me.grayingout.database.entities.MemberWarning;
import me.grayingout.util.EmbedFactory;
import me.grayingout.util.Paging;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

/**
 * Wrapper for a warnings list message
 */
public final class WarningsListMessage {

    /**
     * The page size of the warnings list
     */
    public static final int WARNINGS_PAGE_SIZE = 5;

    /**
     * The warnings list message
     */
    private final Message message;

    /**
     * The member this warnings list is for
     */
    private final Member member;

    /**
     * The current page
     */
    private int page;

    /**
     * Creates a new {@code WarningsListMessage}
     * 
     * @param message The associated message
     */
    public WarningsListMessage(Message message, Member member) {
        this.message = message;
        this.member = member;
        page = 1;
    }

    /**
     * Gets the message associated with this {@code WarningsListMessage}
     * 
     * @return The message
     */
    public final Message getMessage() {
        return message;
    }

    /**
     * Shows the previous page of warnings
     */
    public final void prevPage() {
        showPage(page - 1);
    }

    /**
     * Shows the next page of warnings
     */
    public final void nextPage() {
        showPage(page + 1);
    }

    /**
     * Updates the page shown on the warnings list message
     */
    public final void refresh() {
        showPage(page);
    }

    /**
     * Shows a page of warnings
     * 
     * @param page The page to show
     */
    public final void showPage(int page) {
        /* Get the member's warnings */
        List<MemberWarning> warnings = DatabaseAccessorManager.getWarningsDatabaseAccessor()
        .getMemberWarnings(member);

        /* Make sure page is within bounds */
        int boundedPage = Paging.boundPage(page, WARNINGS_PAGE_SIZE, warnings.size());

        /* Edit message */
        message.editMessage(
        new MessageEditBuilder()
            .setEmbeds(createWarningsListMessageEmbed(member, warnings, boundedPage))
            .setActionRow(getActionRowButtons(boundedPage, warnings.size()))
            .build()
        ).queue();
    }

    /**
     * Creates the {@code MessageCreateData} for a warnings list
     * 
     * @param member The member the list is for
     * @return The {@code MessageCreateData}
     */
    public static final MessageCreateData createWarningsListMessageData(Member member) {
        List<MemberWarning> warnings = DatabaseAccessorManager.getWarningsDatabaseAccessor()
            .getMemberWarnings(member);

        return new MessageCreateBuilder()
            .setEmbeds(createWarningsListMessageEmbed(member, warnings, 1))
            .setActionRow(getActionRowButtons(1, warnings.size()))
            .build();
    }

    /**
     * Creates an embed for a page of warnings
     * 
     * @param member    The warned member
     * @param warnings  The warnings
     * @param page      The current page
     * @return The built embed
     */
    private static final MessageEmbed createWarningsListMessageEmbed(Member member, List<MemberWarning> warnings, int page) {
        /* Get indexes of the start and end of the page */
        int pageStartIndex = Paging.getPageStartIndex(page, WARNINGS_PAGE_SIZE, warnings.size());
        int pageEndIndex = Paging.getPageEndIndex(page, WARNINGS_PAGE_SIZE, warnings.size());
        
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
                Paging.getNumberOfPages(WARNINGS_PAGE_SIZE, warnings.size())
            ),
            warningFields.toArray(new Field[] {})
        );
    }

    /**
     * Gets the action row buttons for the message
     * 
     * @param numberOfWarnings The number of warnings
     * @return The list of buttons
     */
    private static final List<ItemComponent> getActionRowButtons(int page, int numberOfWarnings) {
        Button prevPageButton = Button.primary("warnings_list_prev_page", "Prev Page");
        Button nextPageButton = Button.primary("warnings_list_next_page", "Next Page");
        Button refreshButton = Button.secondary("warnings_list_refresh_page", "Refresh");

        /* Check if on first page */
        if (page <= 1) {
            prevPageButton = prevPageButton.asDisabled();
        }

        /* Check if on last page */
        if (page >= Paging.getNumberOfPages(WARNINGS_PAGE_SIZE, numberOfWarnings)) {
            nextPageButton = nextPageButton.asDisabled();
        }

        return Arrays.asList(prevPageButton, nextPageButton, refreshButton);
    }
}
