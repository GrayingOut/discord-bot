package me.grayingout.bot.commands.implementations.audio;

import me.grayingout.bot.audioplayer.GuildAudioPlayerManager;
import me.grayingout.bot.audioplayer.handler.AudioLoadResult;
import me.grayingout.bot.audioplayer.handler.AudioLoadResultType;
import me.grayingout.bot.commands.BotCommand;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.channel.ChannelType;
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
            .addOption(OptionType.STRING, "url", "The audio url", true)
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        /* Command can only be used in a VC chat */
        if (!event.getChannelType().equals(ChannelType.VOICE)) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Invalid Channel", "This command can only be used in a voice channel chat")
            ).queue();
            return;
        }
        
        /* Check user is in channel */
        GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        if (memberVoiceState.getChannel() == null) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Invalid Audio Channel", "You must be in a voice channel to play audio")
            ).queue();
            return;
        }

        /* Check in voice channel */
        GuildVoiceState selfVoiceState = event.getGuild().getSelfMember().getVoiceState();
        if (selfVoiceState.getChannel() == null) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Invalid Audio Channel", "I must be in a voice channel to play audio")
            ).queue();
            return;
        }

        /* Check member in same channel */
        if (selfVoiceState.getChannel().getIdLong() != memberVoiceState.getChannel().getIdLong()) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Invalid Audio Channel", "You must be in the same voice channel as me")
            ).queue();
            return;
        }

        /* Load the audio */
        AudioLoadResult loadResult = GuildAudioPlayerManager
            .getInstance()
            .getGuildAudioPlayer(event.getGuild())
            .queueAudioByURL(event.getOption("url").getAsString());
        
        /* Track queued */
        if (loadResult.getResultType().equals(AudioLoadResultType.TRACK_LOADED)) {
            event.getHook().sendMessageEmbeds(EmbedFactory.createSuccessEmbed(
                "Audio has been Queued",
                "The requested audio has been added to the queue",
                new Field[] {
                    new Field("Title", loadResult.getLoadedAudioTrack().getInfo().title, false),
                    new Field("Author", loadResult.getLoadedAudioTrack().getInfo().author, false)
                }
            )).queue();
            return;
        }

        /* No math */
        if (loadResult.getResultType().equals(AudioLoadResultType.NO_MATCH)) {
            event.getHook().sendMessageEmbeds(EmbedFactory.createWarningEmbed(
                "Audio Not Found",
                "The requested audio was not found"
            )).queue();
            return;
        }
        
        /* Error */
        event.getHook().sendMessageEmbeds(EmbedFactory.createWarningEmbed(
            "An Error Occurred",
            "An error occurred queueing the requested audio. Please try again."
        )).queue();
    }
}
