package me.grayingout.bot.commands.implementations;

import java.util.List;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.accessor.DatabaseAccessorManager;
import me.grayingout.database.objects.GuildMemberLevelExperience;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command used to get the top 5 levels of
 * members in a guild
 */
public class LevelTopCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("level-top", "Get the top levels in the server")
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

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
                /* Add field */
                fields[i-1] = new Field(
                    trophy + " <vacant>",
                    "*Could this be you?*",
                    false
                );
                continue;
            }

            GuildMemberLevelExperience experience = experiences.get(i-1);

            /* Add field */
            fields[i-1] = new Field(
                trophy + " " + experience.getLevel() + " (" + experience.getExperience() + ")",
                experience.getMember().getAsMention(),
                false
            );
        }

        /* Send embed */
        event.getHook().sendMessageEmbeds(
            EmbedFactory.createGenericEmbed(
                "🏆 Top 5 Server Member Levels",
                "A list of the top 5 server members ordered by how much level experience they have",
                fields
            )
        ).queue();
    }   
}
