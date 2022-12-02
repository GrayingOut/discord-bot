package me.grayingout.commands.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.grayingout.commands.BotCommand;
import me.grayingout.database.warnings.MemberWarning;
import me.grayingout.database.warnings.MemberWarningsListMessage;
import me.grayingout.database.warnings.WarningsDatabase;
import me.grayingout.util.EmbedFactory;
import me.grayingout.util.Warnings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

/**
 * A slash command to get the warnings of a guild
 * member - restricted to {@code Permission.MODERATE_MEMBERS}
 */
public class WarningsCommand extends BotCommand {

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
                Warnings.createMemberNotFoundEmbed()
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
        List<ItemComponent> buttons = Warnings.getNavigationButtons(1, numberOfWarnings);

        /* Create the message */
        MessageCreateData messageCreateData = new MessageCreateBuilder()
            .addEmbeds(Warnings.createWarningsPageEmbed(event.getJDA(), member, warnings, 1))
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
                                .setEmbeds(Warnings.createMemberNotFoundEmbed())
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
                    if (page > Warnings.getNumberOfPages(warnings.size())) {
                        page = Warnings.getNumberOfPages(warnings.size());
                    }
    
                    /* Update the page in the database */
                    boolean success = WarningsDatabase.updateMemberWarningsListMessagePage(event.getMessage(), page);
                    if (!success) {
                        return;
                    }
                    
                    /* Edit the message to show the new embed and buttons */
                    Warnings.updateWarningsListMessage(event.getMessage(), member, warnings, page);
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
                                .setEmbeds(Warnings.createMemberNotFoundEmbed())
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
                    if (newPage > Warnings.getNumberOfPages(warnings.size())) {
                        newPage = Warnings.getNumberOfPages(warnings.size());
                    }

                    /* Update the page in the database */
                    boolean success = WarningsDatabase.updateMemberWarningsListMessagePage(event.getMessage(), newPage);
                    if (!success) {
                        return;
                    }
                    
                    /* Edit the message to show the new embed and buttons */
                    Warnings.updateWarningsListMessage(event.getMessage(), member, warnings, newPage);
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
                                .setEmbeds(Warnings.createMemberNotFoundEmbed())
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
                    Warnings.updateWarningsListMessage(event.getMessage(), member, warnings, newPage);
                });
                break;
            }
        }
    }
}
