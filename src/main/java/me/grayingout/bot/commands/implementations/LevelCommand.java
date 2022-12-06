package me.grayingout.bot.commands.implementations;

import java.awt.Color;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.accessor.DatabaseAccessorManager;
import me.grayingout.database.objects.GuildMemberLevelExperience;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command used to get the level of a guild member
 */
public class LevelCommand extends BotCommand {

    /**
     * The base string for the progress string
     */
    private static final String PROGRESS_STRING_BASE = "----------";

    @Override
    public CommandData getCommandData() {
        return Commands.slash("level", "Get your level or another members level")
            .addOption(OptionType.USER, "member", "The member to get the level of", false)
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

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
        
        MessageEmbed embed = new EmbedBuilder()
            .setColor(Color.MAGENTA)
            .setTitle("📈 Level Report for " + member.getUser().getAsTag() + " (" + member.getId() + ")")
            .addField("Level", String.format(
                "%s (%s)",
                experience.getLevel(),
                experience.getExperience()
            ), false)
            .addField("Progress", getProgressString(
                experience.getCurrentLevelExperience(),
                experience.getNextLevelExperience(),
                experience.getExperience()
            ), false)
            .build();
        
        event.getHook().sendMessageEmbeds(embed).queue();
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
