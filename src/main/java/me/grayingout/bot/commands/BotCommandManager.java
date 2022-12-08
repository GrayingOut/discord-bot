package me.grayingout.bot.commands;

import me.grayingout.bot.commands.implementations.BulkDeleteCommand;
import me.grayingout.bot.commands.implementations.HelloCommand;
import me.grayingout.bot.commands.implementations.PlayAudioCommand;
import me.grayingout.bot.commands.implementations.LevelRolesCommand;
import me.grayingout.bot.commands.implementations.LevelsCommand;
import me.grayingout.bot.commands.implementations.LoggingCommand;
import me.grayingout.bot.commands.implementations.RulesCommand;
import me.grayingout.bot.commands.implementations.SlowmodeCommand;
import me.grayingout.bot.commands.implementations.WarningsCommand;
import me.grayingout.bot.commands.implementations.WelcomeMessageCommand;

/**
 * Stores references to all bot commands
 */
public final class BotCommandManager {
    /**
     * The {@code /hello [user:user]} slash command
     */
    public static final HelloCommand HELLO_COMMAND = new HelloCommand();

    /**
     * The {@code /rules} slash command
     */
    public static final RulesCommand RULES_COMMAND = new RulesCommand();

    /**
     * The {@code /bulk-delete <count:integer>} slash command
     */
    public static final BulkDeleteCommand BULK_DELETE_COMMAND = new BulkDeleteCommand();

    /**
     * The {@code /slowmode <seconds:integer>} slash command
     */
    public static final SlowmodeCommand SLOWMODE_COMMAND = new SlowmodeCommand();

    /**
     * The {@code /warn <member:user> [reason:string]} slash command
     */
    public static final WarningsCommand WARNINGS_COMMAND = new WarningsCommand();

    /**
     * The {@code /set-logging-channel <channel:channel>} slash command
     */
    public static final LoggingCommand LOGGING_COMMAND = new LoggingCommand();

    /**
     * The {@code /level [member:user]} slash command
     */
    public static final LevelsCommand LEVELS_COMMAND = new LevelsCommand();

    /**
     * The {@code /add-level-role <role:role> <level:int>} command
     */
    public static final LevelRolesCommand LEVEL_ROLES_COMMAND = new LevelRolesCommand();

    /**
     * The {@code /welcome-message (set-channel <channel:channel> | remove-channel | set-message <message:string>)} command
     */
    public static final WelcomeMessageCommand WELCOME_MESSAGE_COMMAND = new WelcomeMessageCommand();

    /**
     * The {@code /play <url:string>} command
     */
    public static final PlayAudioCommand PLAY_AUDIO_COMMAND = new PlayAudioCommand();
}
