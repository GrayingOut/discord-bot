package me.grayingout.bot.commands.implementations.audio;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.util.Audio;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
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

        /* Check the execution environment */
        if (!Audio.checkValidCommandExecutionState(event, false)) {
            return;
        }

        /* Attempt to join member */
        Audio.connectToMembersAudioChannel(event.getMember());
        AudioChannelUnion joinedChannel = event.getMember().getVoiceState().getChannel();

        /* Send success response */
        event.getHook().sendMessageEmbeds(
            EmbedFactory.createSuccessEmbed(
                "Joined Channel",
                "Bot has joined " + joinedChannel.getAsMention()
            )
        ).queue();
    }
}
