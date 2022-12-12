package me.grayingout.bot.commands.implementations.audio;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.bot.interactables.playingaudio.PlayingAudioMessage;
import me.grayingout.bot.interactables.playingaudio.PlayingAudioMessageManager;
import me.grayingout.util.Audio;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * A slash command to get the currently playing audio
 */
public final class PlayingCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("playing", "Get the currently playing audio");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        if (!Audio.checkValidCommandExecutionState(event, true)) {
            return;
        }

        MessageCreateData data = PlayingAudioMessage.createPlayingAudioMessageData(event.getGuild());

        event.getHook().sendMessage(data).queue(m -> {
            PlayingAudioMessageManager.register(new PlayingAudioMessage(m));
        });
    }
}
