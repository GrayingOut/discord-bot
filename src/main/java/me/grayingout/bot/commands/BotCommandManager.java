package me.grayingout.bot.commands;

import java.util.HashMap;
import java.util.stream.Collectors;

import me.grayingout.bot.commands.implementations.HelloCommand;
import me.grayingout.bot.commands.implementations.LoggingCommand;
import me.grayingout.bot.commands.implementations.RulesCommand;
import me.grayingout.bot.commands.implementations.WelcomeMessageCommand;
import me.grayingout.bot.commands.implementations.audio.JoinCommand;
import me.grayingout.bot.commands.implementations.audio.LeaveCommand;
import me.grayingout.bot.commands.implementations.audio.PlayCommand;
import me.grayingout.bot.commands.implementations.audio.PlayingCommand;
import me.grayingout.bot.commands.implementations.audio.QueueCommand;
import me.grayingout.bot.commands.implementations.audio.SearchCommand;
import me.grayingout.bot.commands.implementations.audio.StopCommand;
import me.grayingout.bot.commands.implementations.levelling.LevelRolesCommand;
import me.grayingout.bot.commands.implementations.levelling.LevelsCommand;
import me.grayingout.bot.commands.implementations.moderation.BulkDeleteCommand;
import me.grayingout.bot.commands.implementations.moderation.SlowmodeCommand;
import me.grayingout.bot.commands.implementations.moderation.WarningsCommand;
import net.dv8tion.jda.api.JDA;

/**
 * Manages the {@code BotCommand}s
 */
public final class BotCommandManager {

    /**
     * Stores the {@code BotCommand}s
     */
    private static final HashMap<String, BotCommand> botCommands = new HashMap<String, BotCommand>() {{
        put("bulk-delete", new BulkDeleteCommand());
        put("hello", new HelloCommand());
        put("level-roles", new LevelRolesCommand());
        put("levels", new LevelsCommand());
        put("logging", new LoggingCommand());
        put("rules", new RulesCommand());
        put("slowmode", new SlowmodeCommand());
        put("warnings", new WarningsCommand());
        put("welcome-message", new WelcomeMessageCommand());
        put("play", new PlayCommand());
        put("join", new JoinCommand());
        put("leave", new LeaveCommand());
        put("search", new SearchCommand());
        put("stop", new StopCommand());
        put("playing", new PlayingCommand());
        put("queue", new QueueCommand());
    }};

    /**
     * Private constructor
     */
    private BotCommandManager() {}

    /**
     * Gets a {@code BotCommand} by its command name
     * 
     * @param name The name of the command
     * @return The command or {@code null} if no command exists
     */
    public static final BotCommand getBotCommand(String name) {
        return botCommands.get(name);
    }

    /**
     * Adds all commands to the provided JDA instance
     * 
     * @param jda The jda
     */
    public static final void updateJDACommands(JDA jda) {
        jda.updateCommands()
            .addCommands(
                botCommands.values()
                    .stream()
                    .map(c -> c.getCommandData())
                    .collect(Collectors.toList())
            ).queue();
    }
}
