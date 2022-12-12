package me.grayingout.bot.commands.implementations.levelling;

import java.util.List;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.accessors.DatabaseAccessorManager;
import me.grayingout.database.entities.GuildMemberLevelExperience;
import me.grayingout.util.EmbedFactory;
import me.grayingout.util.Levelling;
import me.grayingout.util.SlashCommands;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A slash command used to get and set levels, and view the top
 * levels in the guild - The top level command is not restricted
 * but some subcommands have restrictions
 */
public class LevelsCommand extends BotCommand {

    /**
     * The base string for the progress string
     */
    private static final String PROGRESS_STRING_BASE = "----------";

    @Override
    public CommandData getCommandData() {
        return Commands.slash("levels", "View and set levels")
            .addSubcommands(
                new SubcommandData("get", "Get a member's level")
                    .addOption(OptionType.USER, "member", "The member to get the level of", false),
                new SubcommandData("top", "Get the top 5 levels in the guild"),
                new SubcommandData("set", "Set the level of a member")
                    .addOption(OptionType.USER, "member", "The member to get the level of", true)
                    .addOption(OptionType.INTEGER, "level", "The level to set to", true)
            )
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        switch (event.getSubcommandName()) {
            case "get": {
                /* Get the member to get the level of */
                Member member;
                if (event.getOption("member") != null) {
                    member = event.getOption("member").getAsMember();
                } else {
                    member = event.getMember();
                }

                /* Get level information */
                GuildMemberLevelExperience experience = DatabaseAccessorManager
                    .getLevellingDatabaseAccessor()
                    .getGildMemberLevelExperience(member);
                
                /* Create fields */
                Field[] fields = {
                    new Field("Level", String.format(
                        "%s (%s)",
                        experience.getLevel(),
                        experience.getExperience()
                    ), false),
                    new Field("Progress", getProgressString(
                        experience.getCurrentLevelExperience(),
                        experience.getNextLevelExperience(),
                        experience.getExperience()
                    ), false)
                };
                
                /* Send level report response */
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createGenericEmbed(
                        "📈 Level Report for " + member.getUser().getAsTag() + " (" + member.getId() + ")",
                        "",
                        fields
                    )
                ).queue();
                break;
            }
            case "top": {
                /* Get the top 5 members */
                List<GuildMemberLevelExperience> experiences =
                DatabaseAccessorManager.getLevellingDatabaseAccessor().getTopGuildMembers(event.getGuild());
            
                Field[] fields = new Field[5];
                
                /* Add members */
                for (int i = 1; i <= 5; i++) {
                    /* Get the place trophy */
                    String trophy = i == 1 ? ":first_place:" : i == 2 ? ":second_place:" : i == 3 ? ":third_place:" : "";
                    
                    /* No one in position */
                    if (i > experiences.size()) {
                        /* Add empty field */
                        fields[i-1] = new Field(
                            trophy + " <vacant>",
                            "*Could this be you?*",
                            false
                        );
                        continue;
                    }

                    /* Get the member's experience */
                    GuildMemberLevelExperience experience = experiences.get(i-1);

                    /* Add level field */
                    fields[i-1] = new Field(
                        trophy + " " + experience.getLevel() + " (" + experience.getExperience() + ")",
                        experience.getMember().getAsMention(),
                        false
                    );
                }

                /* Send top levels response */
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createGenericEmbed(
                        "🏆 Top 5 Server Member Levels",
                        "A list of the top 5 server members ordered by how much level experience they have",
                        fields
                    )
                ).queue();
                break;
            }
            case "set": {
                /* Check for additional permission */
                if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    event.getHook().sendMessageEmbeds(
                        EmbedFactory.createErrorEmbed("Insufficient Permission", "This subcommand requires the `ADMINISTRATOR` permission.")
                    ).queue();
                    break;
                }

                /* Get the level to set to */
                Integer level = SlashCommands.safelyGetIntOption(event, "level");
                if (level == null) {
                    /* Invalid integer */
                    event.getHook().sendMessageEmbeds(EmbedFactory.createInvalidIntegerOptionEmbed("level")).queue();
                    return;
                }

                /* Check valid level */
                if (level < 0) {
                    event.getHook().sendMessageEmbeds(
                        EmbedFactory.createWarningEmbed("Invalid Argument", "`level` must be greater than 0")
                    ).queue();
                    return;
                }

                /* Get the member to set */
                Member member = event.getOption("member").getAsMember();

                /* Set the member's new level */
                DatabaseAccessorManager.getLevellingDatabaseAccessor()
                    .setGuildMemberLevelExperience(member, Levelling.getExperienceForLevel(level));

                /* Send response message */
                event.getHook().sendMessageEmbeds(
                    EmbedFactory.createSuccessEmbed("Updated Members Level", "The member's level has been set to `" + level + "`")
                ).queue();
                break;
            }
        }

        
    }
    
    /**
     * Creates the embed progress string
     * 
     * @param prevExperience Experience needed for previous level
     * @param nextExperience Experience needed for next level
     * @param experience     The current experience
     * @return The string
     */
    private final String getProgressString(int currentExperience, int nextExperience, int experience) {
        StringBuilder builder = new StringBuilder();

        double progress = (experience-currentExperience)/(nextExperience-currentExperience * 1.0);

        builder.append(PROGRESS_STRING_BASE);
        builder.setCharAt((int) Math.floor(progress*PROGRESS_STRING_BASE.length()), 'o');
        builder.insert(0, Integer.toString(currentExperience) + " ");
        builder.append(" " + Integer.toString(nextExperience));
        builder.append(" **(" + String.format("%.0f%%", progress*100) + ")**");

        return builder.toString().replaceAll("-", ":heavy_minus_sign:").replace("o", ":black_circle:");
    }
}
