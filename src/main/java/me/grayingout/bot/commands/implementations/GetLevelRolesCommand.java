package me.grayingout.bot.commands.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.database.accessor.DatabaseAccessorManager;
import me.grayingout.database.objects.GuildLevelRole;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command used to view the list of level roles
 */
public final class GetLevelRolesCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("get-level-roles", "Get a list of level roles")
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        /* Get the roles */
        List<GuildLevelRole> levelRoles = DatabaseAccessorManager
            .getLevellingDatabaseAccessor()
            .getGuildLevelRoles(event.getGuild());
        
        /* Store the fields */
        List<Field> fields = new ArrayList<>();

        /* Sort the roles in order of required level in descending order */
        levelRoles = levelRoles.stream()
            .sorted(new GuildLevelRole.GuildLevelRoleComparator(false))
            .collect(Collectors.toList());
        
        int previousRequiredLevel = -1;

        /* This basically combines roles with same required level together */
        for (GuildLevelRole levelRole : levelRoles) {
            if (levelRole.getRequiredLevel() != previousRequiredLevel) {
                previousRequiredLevel = levelRole.getRequiredLevel();
                fields.add(new Field(
                    "Level: " + previousRequiredLevel,
                    levelRole.getRole().getAsMention(),
                    false
                ));
                continue;
            }

            Field oldField = fields.get(fields.size()-1);

            fields.set(fields.size()-1, new Field(
                oldField.getName(),
                oldField.getValue() + "\n" + levelRole.getRole().getAsMention(),
                oldField.isInline()
            ));
        }

        /* Create response embed */
        MessageEmbed embed = EmbedFactory.createGenericEmbed(
            "🎭 Level Role Rewards",
            "The following roles are available at the following levels",
            fields.toArray(new Field[] {}));
        
        /* Send response */
        event.getHook().sendMessageEmbeds(embed).queue();
    }
}
