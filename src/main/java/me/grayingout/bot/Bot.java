package me.grayingout.bot;

import java.util.stream.Collectors;

import me.grayingout.bot.commands.BotCommandManager;
import me.grayingout.bot.events.LevellingEventsHandler;
import me.grayingout.bot.events.MessageCache;
import me.grayingout.bot.events.interactions.WarningsListInteractionHandler;
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
                new WarningsListInteractionHandler(),
                new DeletedMessageLogger(),
                new LevellingEventsHandler(),
                MessageCache.getInstance()
            )
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .setEventPassthrough(true)
            .setActivity(Activity.competing("World Domination"))
            .build();

        /* Wait for the JDA to be ready */
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /* Update the global commands */
        jda.updateCommands()
            .addCommands(
                BotCommandManager.HELLO_COMMAND.getCommandData(),
                BotCommandManager.RULES_COMMAND.getCommandData(),
                BotCommandManager.BULK_DELETE_COMMAND.getCommandData(),
                BotCommandManager.SLOWMODE_COMMAND.getCommandData(),
                BotCommandManager.WARN_COMMAND.getCommandData(),
                BotCommandManager.WARNINGS_COMMAND.getCommandData(),
                BotCommandManager.CLEAR_WARNINGS_COMMAND.getCommandData(),
                BotCommandManager.REMOVE_WARNING_COMMAND.getCommandData(),
                BotCommandManager.SET_LOGGING_CHANNEL_COMMAND.getCommandData(),
                BotCommandManager.LEVEL_COMMAND.getCommandData(),
                BotCommandManager.SET_LEVEL_COMMAND.getCommandData(),
                BotCommandManager.LEVEL_TOP_COMMAND.getCommandData(),
                BotCommandManager.ADD_LEVEL_ROLE_COMMAND.getCommandData(),
                BotCommandManager.GET_LEVEL_ROLES_COMMAND.getCommandData()
            ).queue();
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

        /* Check which slash command has been used */
        switch (event.getName()) {
            case "hello":
                /* Say hello to the bot */
                BotCommandManager.HELLO_COMMAND.execute(event);
                break;
            case "rules":
                /* Send the rules embed to a channel */
                BotCommandManager.RULES_COMMAND.execute(event);
                break;
            case "bulk-delete":
                /* Delete multiple messages at once */
                BotCommandManager.BULK_DELETE_COMMAND.execute(event);
                break;
            case "slowmode":
                /* Change the slowmode of a channel */
                BotCommandManager.SLOWMODE_COMMAND.execute(event);
                break;
            case "warn":
                /* Warn a member */
                BotCommandManager.WARN_COMMAND.execute(event);
                break;
            case "warnings":
                /* Get list of members warnings */
                BotCommandManager.WARNINGS_COMMAND.execute(event);
                break;
            case "clear-warnings":
                /* Delete a member's warnings */
                BotCommandManager.CLEAR_WARNINGS_COMMAND.execute(event);
                break;
            case "remove-warning":
                /* Delete a specific warning from a member */
                BotCommandManager.REMOVE_WARNING_COMMAND.execute(event);
                break;
            case "set-logging-channel":
                BotCommandManager.SET_LOGGING_CHANNEL_COMMAND.execute(event);
                break;
            case "level":
                BotCommandManager.LEVEL_COMMAND.execute(event);
                break;
            case "set-level":
                BotCommandManager.SET_LEVEL_COMMAND.execute(event);
                break;
            case "level-top":
                BotCommandManager.LEVEL_TOP_COMMAND.execute(event);
                break;
            case "add-level-role":
                BotCommandManager.ADD_LEVEL_ROLE_COMMAND.execute(event);
                break;
            case "get-level-roles":
                BotCommandManager.GET_LEVEL_ROLES_COMMAND.execute(event);
                break;
            default:
                throw new RuntimeException("Unhandled slash command: " + event.getName());
        }
    }
}
