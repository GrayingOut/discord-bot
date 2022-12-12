package me.grayingout.bot.commands.implementations.audio;

import me.grayingout.bot.audioplayer.GuildAudioPlayerManager;
import me.grayingout.bot.audioplayer.handler.AudioLoadResult;
import me.grayingout.bot.commands.BotCommand;
import me.grayingout.util.Audio;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command used to search for an audio
 */
public final class SearchCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("search", "Search for an audio on YouTube")
            .addOption(OptionType.STRING, "search", "The search term to search on YouTube", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        /* Check the execution environment */
        if (!Audio.checkValidCommandExecutionState(event, true)) {
            return;
        }

        /* Search for the audio */
        AudioLoadResult loadResult = GuildAudioPlayerManager
            .getInstance()
            .getGuildAudioPlayer(event.getGuild())
            .queueAudioByYTSearch(event.getOption("search").getAsString());
        
        /* Handle the response */
        Audio.handleAudioLoadResult(event, loadResult);
    }    
}
