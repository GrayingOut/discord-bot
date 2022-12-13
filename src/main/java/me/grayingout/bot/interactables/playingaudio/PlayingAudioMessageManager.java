package me.grayingout.bot.interactables.playingaudio;

import java.util.HashMap;

import me.grayingout.bot.audioplayer.GuildAudioPlayer;
import me.grayingout.bot.audioplayer.GuildAudioPlayerManager;
import me.grayingout.util.Audio;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Manages the playing audio messages to make them interactable
 */
public final class PlayingAudioMessageManager extends ListenerAdapter {

    /**
     * Stores the {@code AudioQueueMessage}s against their message long id
     */
    private static final HashMap<Long, PlayingAudioMessage> messages = new HashMap<>();

    /**
     * Registers an {@code AudioQueueMessage} on the manager
     * 
     * @param playingAudioMessage The {@code AudioQueueMessage}
     */
    public static final void register(PlayingAudioMessage playingAudioMessage) {
        messages.put(playingAudioMessage.getMessage().getIdLong(), playingAudioMessage);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getButton().getId()) {
            case "playing_audio_refresh": {
                event.deferEdit().queue();
                if (messages.get(event.getMessageIdLong()) == null) break;

                messages.get(event.getMessageIdLong()).refresh();
                break;
            }
            case "playing_audio_skip": {
                event.deferEdit().queue();
                if (messages.get(event.getMessageIdLong()) == null) break;

                GuildAudioPlayerManager.getInstance().getGuildAudioPlayer(event.getGuild()).skip();

                messages.get(event.getMessageIdLong()).refresh();
                break;
            }
            case "playing_audio_loop": {
                /* Check member is a DJ */
                if (!Audio.isMemberAValidDJ(event.getMember())) {
                    event.deferReply(true).queue();
                    event.getHook().sendMessageEmbeds(EmbedFactory.createNotADJEmbed()).queue();
                    break;
                }
                
                event.deferEdit().queue();
                if (messages.get(event.getMessageIdLong()) == null) break;

                GuildAudioPlayer guildAudioPlayer = GuildAudioPlayerManager.getInstance()
                    .getGuildAudioPlayer(event.getGuild());
                
                /* Toggle loop */
                if (guildAudioPlayer.isLooping()) {
                    guildAudioPlayer.disableLoop();
                    messages.get(event.getMessageIdLong()).refresh();
                    return;
                }
                guildAudioPlayer.enableLoop();
                messages.get(event.getMessageIdLong()).refresh();
            }
        }
    }
}
