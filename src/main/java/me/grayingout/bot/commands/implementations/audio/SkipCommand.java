package me.grayingout.bot.commands.implementations.audio;

import me.grayingout.bot.audioplayer.GuildAudioPlayer;
import me.grayingout.bot.audioplayer.GuildAudioPlayerManager;
import me.grayingout.bot.audioplayer.skip.GuildSkipAudio;
import me.grayingout.bot.audioplayer.skip.GuildSkipAudioManager;
import me.grayingout.bot.commands.BotCommand;
import me.grayingout.util.Audio;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command to skip a playing audio
 */
public final class SkipCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("skip", "Skip the currently playing audio");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        /* Check execution environment */
        if (!Audio.checkValidCommandExecutionState(event, true)) {
            return;
        }

        GuildAudioPlayer guildAudioPlayer = GuildAudioPlayerManager.getInstance()
            .getGuildAudioPlayer(event.getGuild());

        /* Check an audio is playing */
        if (guildAudioPlayer.getPlayingAudioTrack() == null) {
            event.deferReply(true).queue();
            event.getHook().sendMessageEmbeds(EmbedFactory.createWarningEmbed(
                "No Audio Playing",
                "There needs to be an audio playing to use this command"
            )).queue();
            return;
        }

        GuildSkipAudio guildSkipAudio = GuildSkipAudioManager.getInstance()
            .getGuildSkipAudio(event.getGuild());

        /* Check if member has already voted */
        if (guildSkipAudio.hasMemberAlreadyVotedToSkip(event.getMember())) {
            event.deferReply(true).queue();
            event.getHook().sendMessageEmbeds(EmbedFactory.createErrorEmbed(
                "Already Voted",
                "You have already voted to skip the audio"
            )).queue();
            return;
        }
        
        event.deferReply().queue();

        /* Add member's vote */
        boolean skipSuccess = guildSkipAudio.addVoteSkip(event.getMember());

        /* Send skip vote confirmation */
        event.getHook().sendMessageEmbeds(EmbedFactory.createGenericEmbed(
            "📀 Voted to Skip",
            String.format(
                "%s has voted to skip **(%s/%s)**\nUse `/skip` to participate",
                event.getMember().getAsMention(),
                guildSkipAudio.getCurrentVoteSkips(),
                guildSkipAudio.getVoteSkipsThreshold())
        )).queue();

        /* Check of vote successful */
        if (skipSuccess) {
            guildAudioPlayer.skip();
    
            event.getChannel().sendMessageEmbeds(EmbedFactory.createSuccessEmbed(
                "Audio Skipped",
                "A vote skip has been successful. The audio has been skipped"
            )).queue();
            return;
        }
    }
}
