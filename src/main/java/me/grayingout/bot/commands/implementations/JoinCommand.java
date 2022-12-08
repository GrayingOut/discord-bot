package me.grayingout.bot.commands.implementations;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command to have the bot join a voice channel
 */
public final class JoinCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("join", "Make the bot join your current voice channel");
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
                EmbedFactory.createWarningEmbed("Invalid Audio Channel", "You must be in a voice channel to use this command")
            ).queue();
            return;
        }

        /* Join channel */
        event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());

        /* Send success response */
        event.getHook().sendMessageEmbeds(
            EmbedFactory.createSuccessEmbed(
                "Joined Channel",
                "Bot has joined " + memberVoiceState.getChannel().getAsMention()
            )
        ).queue();
    }
    
}
