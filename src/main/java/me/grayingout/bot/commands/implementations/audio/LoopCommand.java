package me.grayingout.bot.commands.implementations.audio;

import me.grayingout.bot.audioplayer.GuildAudioPlayer;
import me.grayingout.bot.audioplayer.GuildAudioPlayerManager;
import me.grayingout.bot.commands.BotCommand;
import me.grayingout.util.Audio;
import me.grayingout.util.EmbedFactory;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public final class LoopCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("loop", "Sets whether audio looping is enabled")
            .addOption(OptionType.BOOLEAN, "enabled", "If looping is enabled", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        /* Check member is a DJ */
        if (!Audio.isMemberAValidDJ(event.getMember())) {
            event.deferReply(true).queue();
            event.getHook().sendMessageEmbeds(EmbedFactory.createNotADJEmbed()).queue();
            return;
        }
        event.deferReply().queue();

        /* Check execution environment */
        if (!Audio.checkValidCommandExecutionState(event, true)) {
            return;
        }

        boolean enabled = event.getOption("enabled").getAsBoolean();

        GuildAudioPlayer guildAudioPlayer = GuildAudioPlayerManager.getInstance()
            .getGuildAudioPlayer(event.getGuild());
        
        /* Enable */
        if (enabled) {
            guildAudioPlayer.enableLoop();
            event.getHook().sendMessageEmbeds(EmbedFactory.createSuccessEmbed(
                "Loop Enabled",
                "The current or next playing track will now loop"
            )).queue();
            return;
        }
        
        /* Disable */
        guildAudioPlayer.disableLoop();
        event.getHook().sendMessageEmbeds(EmbedFactory.createSuccessEmbed(
            "Loop Disable",
            "The current track will no longer loop"
        )).queue();
    }
    
}
