package me.grayingout.bot.commands.implementations.audio;

import me.grayingout.bot.audioplayer.GuildAudioPlayer;
import me.grayingout.bot.audioplayer.GuildAudioPlayerManager;
import me.grayingout.bot.commands.BotCommand;
import me.grayingout.util.Audio;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command used to stop playing all audio and
 * clear the queue
 */
public final class StopCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("stop", "Stop playing audio and clear the queue");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        /* Check member is a DJ */
        if (!Audio.isMemberAValidDJ(event.getMember())) {
            event.getHook().sendMessageEmbeds(EmbedFactory.createNotADJEmbed()).queue();
            return;
        }

        /* Check the execution environment */
        if (!Audio.checkValidCommandExecutionState(event, true)) {
            return;
        }

        GuildAudioPlayer guildAudioPlayer = GuildAudioPlayerManager
            .getInstance()
            .getGuildAudioPlayer(event.getGuild());
        
        /* Stop playing and clear the queue */
        guildAudioPlayer.stopPlaying();
        guildAudioPlayer.clearQueue();

        /* Response */
        event.getHook().sendMessageEmbeds(EmbedFactory.createSuccessEmbed(
            "Audio Stopped",
            "The audio playing has been stopped and the queue has been cleared"
        )).queue();
    }
}
