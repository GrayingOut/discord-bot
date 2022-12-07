package me.grayingout.bot.commands.implementations;

import me.grayingout.bot.audioplayer.GuildAudioPlayerManager;
import me.grayingout.bot.commands.BotCommand;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public final class PlayAudioCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("play", "Play an audio from its url")
            .addOption(OptionType.STRING, "url", "The audio url", true)
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        
        /* Check user is in channel */
        GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        if (memberVoiceState.getChannel() == null) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Invalid Audio Channel", "You must be in a voice channel to play audio")
            ).queue();
            return;
        }

        /* Join channel if not in one */
        GuildVoiceState selfVoiceState = event.getGuild().getSelfMember().getVoiceState();
        if (selfVoiceState.getChannel() == null) {
            event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
            return;
        }

        /* Check member in same channel */
        if (selfVoiceState.getChannel().getIdLong() != memberVoiceState.getChannel().getIdLong()) {
            event.getHook().sendMessageEmbeds(
                EmbedFactory.createWarningEmbed("Invalid Audio Channel", "You must be in the same voice channel as me")
            ).queue();
            return;
        }

        /* Play audio */
        GuildAudioPlayerManager.getInstance().playAudio(
            (GuildMessageChannel) event.getChannel(),
            event.getOption("url").getAsString()
        );
        
        /* Send response message */
        event.getHook().sendMessageEmbeds(
            EmbedFactory.createSuccessEmbed("Audio Queued", "The audio has been queued")
        ).queue();
    }
}
