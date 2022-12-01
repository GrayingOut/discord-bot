package me.grayingout.commands.implementations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.grayingout.commands.BotCommand;
import me.grayingout.database.warnings.MemberWarning;
import me.grayingout.database.warnings.MemberWarningsListMessage;
import me.grayingout.database.warnings.WarningsDatabase;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

/**
 * A slash command to get the warnings of a guild
 * member - restricted to {@code Permission.MODERATE_MEMBERS}
 */
public class WarningsCommand extends BotCommand {

    /**
     * The page size of the warnings list
     */
    private static final double WARNINGS_PAGE_SIZE = 5d;

    @Override
    public CommandData getCommandData() {
        return Commands
            .slash("warnings", "Get list of members warnings")
            .addOption(OptionType.USER, "member", "The member to get warnings for", true)
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS))
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        
        /* Get the member who's warnings to show */
        Member member = event.getOption("member").getAsMember();

        /* Member no longer in guild */
        if (member == null) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Member not Found", "The specified member is no longer in the guild")
            ).queue(message -> {
                message.delete().queueAfter(3, TimeUnit.SECONDS);
            });
            return;
        }

        /* Get member's warnings */
        List<MemberWarning> warnings = WarningsDatabase.getMemberWarnings(member);
        if (warnings == null) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Database Access Error", "Failed to get member warnings. Contact the bot developer.")
            ).queue(message -> {
                message.delete().queueAfter(3, TimeUnit.SECONDS);
            });
            return;
        }

        int numberOfWarnings = warnings.size();

        /* Get the previous and next buttons */
        List<ItemComponent> buttons = getNavigationButtons(1, numberOfWarnings);

        /* Create the message */
        MessageCreateData messageCreateData = new MessageCreateBuilder()
            .addEmbeds(createWarningsPageEmbed(event.getJDA(), member, warnings, 1))
            .addActionRow(buttons)
            .build();

        /* Send the message and update the database on success */
        event.getHook().sendMessage(messageCreateData).queue(message -> {
            /* Add message to database */
            WarningsDatabase.putMemberWarningsListMessage(message, member, 1);
        });
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getButton().getId()) {
            case "warnings_list_refresh_page": {
                event.deferEdit().queue();

                /* Get the warnings list message data */
                MemberWarningsListMessage mwlm = WarningsDatabase.getMemberWarningsListMessage(event.getMessage());
    
                /* Get the member related to the data */
                mwlm.getWarnedMember(event.getGuild())
                .thenAccept(member -> {
                    /* Member not in server */
                    if (member == null) {
                        event.getMessage().editMessage(
                            new MessageEditBuilder()
                                .setEmbeds(EmbedFactory.createWarningEmbed(
                                    "Member not Found",
                                    "The member this warning list belongs to is no longer in the server"
                                ))
                                .setComponents(new ArrayList<>())
                                .build()
                        ).queue(message -> {
                            message.delete().queueAfter(3, TimeUnit.SECONDS);
                        });
                        return;
                    }

                    /* Get the member's warnings */ 
                    List<MemberWarning> warnings = WarningsDatabase.getMemberWarnings(member);
                    if (warnings == null) {
                        return;
                    }
                    
                    /* Get the current page - making sure in range */
                    int page = mwlm.getCurrentPage();
                    if (page < 1) {
                        page = 1;
                    }
                    if (page > getNumberOfPages(warnings.size())) {
                        page = getNumberOfPages(warnings.size());
                    }
    
                    /* Update the page in the database */
                    boolean success = WarningsDatabase.updateMemberWarningsListMessagePage(event.getMessage(), page);
                    if (!success) {
                        return;
                    }
                    
                    /* Edit the message to show the new embed and buttons */
                    event.getMessage().editMessage(
                        new MessageEditBuilder()
                            .setEmbeds(createWarningsPageEmbed(
                                event.getJDA(),
                                member,
                                warnings,
                                page
                            ))
                            .setActionRow(getNavigationButtons(page, warnings.size()))
                            .build()
                    ).queue();
                });
                break;
            }

            case "warnings_list_next_page": {
                event.deferEdit().queue();

                /* Get the warnings list message data */
                MemberWarningsListMessage mwlm = WarningsDatabase.getMemberWarningsListMessage(event.getMessage());
    
                /* Get the member related to the data */
                mwlm.getWarnedMember(event.getGuild())
                .thenAccept(member -> {
                    /* Member not in server */
                    if (member == null) {
                        event.getMessage().editMessage(
                            new MessageEditBuilder()
                                .setEmbeds(EmbedFactory.createWarningEmbed(
                                    "Member not Found",
                                    "The member this warning list belongs to is no longer in the server"
                                ))
                                .setComponents(new ArrayList<>())
                                .build()
                        ).queue(message -> {
                            message.delete().queueAfter(3, TimeUnit.SECONDS);
                        });
                        return;
                    }

                    /* Get the member's warnings */
                    List<MemberWarning> warnings = WarningsDatabase.getMemberWarnings(member);
                    if (warnings == null) {
                        return;
                    }
                    
                    /* Get the new page */
                    int newPage = mwlm.getCurrentPage() + 1;

                    /* Check page is valid */
                    if (newPage > getNumberOfPages(warnings.size())) {
                        newPage = getNumberOfPages(warnings.size());
                    }

                    /* Update the page in the database */
                    boolean success = WarningsDatabase.updateMemberWarningsListMessagePage(event.getMessage(), newPage);
                    if (!success) {
                        return;
                    }
                    
                    /* Edit the message to show the new embed and buttons */
                    event.getMessage().editMessage(
                        new MessageEditBuilder()
                            .setEmbeds(createWarningsPageEmbed(
                                event.getJDA(),
                                member,
                                warnings,
                                newPage
                            ))
                            .setActionRow(getNavigationButtons(newPage, warnings.size()))
                            .build()
                    ).queue();
                });
                break;
            }

            case "warnings_list_prev_page": {
                event.deferEdit().queue();

                /* Get the warnings list message data */
                MemberWarningsListMessage mwlm = WarningsDatabase.getMemberWarningsListMessage(event.getMessage());
    
                /* Get the member related to the data */
                mwlm.getWarnedMember(event.getGuild())
                .thenAccept(member -> {
                    /* Member not in server */
                    if (member == null) {
                        event.getMessage().editMessage(
                            new MessageEditBuilder()
                                .setEmbeds(EmbedFactory.createWarningEmbed(
                                    "Member not Found",
                                    "The member this warning list belongs to is no longer in the server"
                                ))
                                .setComponents(new ArrayList<>())
                                .build()
                        ).queue(message -> {
                            message.delete().queueAfter(3, TimeUnit.SECONDS);
                        });
                        return;
                    }

                    /* Get the member's warnings */
                    List<MemberWarning> warnings = WarningsDatabase.getMemberWarnings(member);
                    if (warnings == null) {
                        return;
                    }
    
                    /* Get the new page */
                    int newPage = mwlm.getCurrentPage() - 1;
    
                    /* Check page is valid */
                    if (newPage < 1) {
                        newPage = 1;
                    }
    
                    /* Update the new page in the database */
                    boolean success = WarningsDatabase.updateMemberWarningsListMessagePage(event.getMessage(), newPage);
                    if (!success) {
                        return;
                    }
    
                    /* Edit the message with new embed and buttons */
                    event.getMessage().editMessage(
                        new MessageEditBuilder()
                            .setEmbeds(createWarningsPageEmbed(
                                event.getJDA(),
                                member,
                                warnings,
                                newPage
                            ))
                            .setActionRow(getNavigationButtons(newPage, warnings.size()))
                            .build()
                    ).queue();
                });
                break;
            }
        }
    }

    /**
     * Create the navigation buttons based on the current page
     * and number of warnings
     * 
     * @param currentPage      The current page
     * @param numberOfWarnings The number of warnings
     * @return The list of buttons
     */
    private final List<ItemComponent> getNavigationButtons(int currentPage, int numberOfWarnings) {
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
     * @param jda       The JDA instance
     * @param member    The warned member
     * @param warnings  The warnings
     * @param page      The current page
     * @return The built embed
     */
    private final MessageEmbed createWarningsPageEmbed(JDA jda, Member member, List<MemberWarning> warnings, int page) {
        /* Get indexes of the start and end of the page */
        int pageStartIndex = getPageStartIndex(page, warnings.size());
        int pageEndIndex = getPageEndIndex(page, warnings.size());
        
        ArrayList<Field> warningFields = new ArrayList<>();
        
        /* Add each field from the current page */
        for (MemberWarning warning : warnings.subList(pageStartIndex, pageEndIndex)) {
            warningFields.add(new Field(
                String.format(
                    ":pen_ballpoint: Warned by **%s** (%s)",
                    warning.getWarnerUser(jda).getAsTag(),
                    warning.getWarnerUserId()),
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
    public final int getNumberOfPages(int numberOfWarnings) {
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
    private final int getPageStartIndex(int page, int numberOfWarnings) {
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
    private final int getPageEndIndex(int page, int numberOfWarnings) {
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
