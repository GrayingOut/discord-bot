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
 * A slash command used to queue audio to be played
 */
public final class PlayCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("play", "Play an audio from its url")
            .addOption(OptionType.STRING, "url", "The url of the audio source", true)
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        /* Check the execution environment */
        if (!Audio.checkValidCommandExecutionState(event, true)) {
            return;
        }

        /* Load the audio */
        AudioLoadResult loadResult = GuildAudioPlayerManager
            .getInstance()
            .getGuildAudioPlayer(event.getGuild())
            .queueAudioByURL(event.getOption("url").getAsString());
        
        /* Handle the response */
        Audio.handleAudioLoadResult(event, loadResult);
    }
}
