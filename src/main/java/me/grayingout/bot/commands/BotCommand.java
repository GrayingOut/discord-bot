package me.grayingout.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/*
 * The base class for a bot command
 */
public abstract class BotCommand {
    
    /**
     * Gets the {@code CommandData} associated with the
     * bot command
     * 
     * @return The {@code CommandData}
     */
    public abstract CommandData getCommandData();

    /**
     * Executes the bot command
     * 
     * @param event The slash command event
     */
    public abstract void execute(SlashCommandInteractionEvent event);
}
