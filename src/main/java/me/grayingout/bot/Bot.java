package me.grayingout.bot;

import java.util.stream.Collectors;

import me.grayingout.bot.commands.BotCommand;
import me.grayingout.bot.commands.BotCommandManager;
import me.grayingout.bot.listeners.LevellingListeners;
import me.grayingout.bot.listeners.WelcomeMessageListeners;
import me.grayingout.bot.listeners.interactions.WarningsListInteractionListeners;
import me.grayingout.bot.logging.DeletedMessageLogger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * The wrapper class for the {@code JDA}
 */
public final class Bot extends ListenerAdapter {

    /**
     * The {@code JDA} instance
     */
    private final JDA jda;
    
    /**
     * Instantiates a new {@code Bot} with the provided token
     * 
     * @param token The bot token
     */
    public Bot(String token) {
        /* Create a new JDA */
        jda = JDABuilder.createDefault(token)
            .addEventListeners(
                this,
                new WarningsListInteractionListeners(),
                new DeletedMessageLogger(),
                new LevellingListeners(),
                new WelcomeMessageListeners(),
                MessageCache.getInstance()
            )
            .enableIntents(
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS
            )
            .setEventPassthrough(true)
            .setActivity(Activity.competing("World Domination"))
            .build();

        /* Wait for the JDA to be ready */
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /* Update the bot commands */
        BotCommandManager.updateJDACommands(jda);
    }

    /**
     * Get the {@code JDA} instance of the bot
     * 
     * @return The bot JDA
     */
    public final JDA getJDA() {
        return jda;
    }

    /**
     * Detect slash command usage
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        /* Log the usage of slash commands */
        System.out.printf(
            "[Slash Command Usage] \u001b[1m%s\u001b[0m has used \u001b[1m/%s\u001b[0m; %s; \n",
            event.getUser().getAsTag(),
            event.getFullCommandName(),
            event.getOptions().stream().map(o -> o.getName() + "=" + event.getOption(o.getName()).getAsString()).collect(Collectors.joining(" "))
        );

        /* Get the command */
        BotCommand command = BotCommandManager.getBotCommand(event.getName());
        if (command != null) {
            command.execute(event);
            return;
        }
        throw new RuntimeException("Unhandled bot command: " + event.getName());
    }
}
