package me.grayingout;

import java.util.stream.Collectors;

import me.grayingout.commands.BotCommandStore;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
                BotCommandStore.WARNINGS_COMMAND
            )
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
                BotCommandStore.HELLO_COMMAND.getCommandData(),
                BotCommandStore.RULES_COMMAND.getCommandData(),
                BotCommandStore.BULK_DELETE_COMMAND.getCommandData(),
                BotCommandStore.SLOWMODE_COMMAND.getCommandData(),
                BotCommandStore.WARN_COMMAND.getCommandData(),
                BotCommandStore.WARNINGS_COMMAND.getCommandData(),
                BotCommandStore.CLEAR_WARNINGS_COMMAND.getCommandData(),
                BotCommandStore.REMOVE_WARNING_COMMAND.getCommandData()
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
                BotCommandStore.HELLO_COMMAND.execute(event);
                break;
            case "rules":
                /* Send the rules embed to a channel */
                BotCommandStore.RULES_COMMAND.execute(event);
                break;
            case "bulk-delete":
                /* Delete multiple messages at once */
                BotCommandStore.BULK_DELETE_COMMAND.execute(event);
                break;
            case "slowmode":
                /* Change the slowmode of a channel */
                BotCommandStore.SLOWMODE_COMMAND.execute(event);
                break;
            case "warn":
                /* Warn a member */
                BotCommandStore.WARN_COMMAND.execute(event);
                break;
            case "warnings":
                /* Get list of members warnings */
                BotCommandStore.WARNINGS_COMMAND.execute(event);
                break;
            case "clear-warnings":
                /* Delete a member's warnings */
                BotCommandStore.CLEAR_WARNINGS_COMMAND.execute(event);
                break;
            case "remove-warning":
                /* Delete a specific warning from a member */
                BotCommandStore.REMOVE_WARNING_COMMAND.execute(event);
                break;
            default:
                throw new RuntimeException("Unhandled slash command: " + event.getName());
        }
    }
}
