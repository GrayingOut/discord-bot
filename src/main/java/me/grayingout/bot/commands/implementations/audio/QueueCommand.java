package me.grayingout.bot.commands.implementations.audio;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.bot.interactables.audioqueue.AudioQueueMessage;
import me.grayingout.bot.interactables.audioqueue.AudioQueueMessageManager;
import me.grayingout.util.Audio;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * A slash command to view the next 5 items in the
 * playing queue
 */
public final class QueueCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("queue", "View the audio queue");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        if (!Audio.checkValidCommandExecutionState(event, true)) {
            return;
        }

        /* Create the message */
        MessageCreateData data = Audio.createAudioQueueMessageMessage(event.getGuild());

        event.getHook().sendMessage(data).queue(m -> {
            /* Register the message */
            AudioQueueMessageManager.register(new AudioQueueMessage(m));
        });
    }
}
