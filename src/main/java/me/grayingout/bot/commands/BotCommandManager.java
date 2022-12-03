package me.grayingout.bot.commands;

import me.grayingout.bot.commands.implementations.BulkDeleteCommand;
import me.grayingout.bot.commands.implementations.ClearWarningsCommand;
import me.grayingout.bot.commands.implementations.HelloCommand;
import me.grayingout.bot.commands.implementations.RemoveWarningCommand;
import me.grayingout.bot.commands.implementations.RulesCommand;
import me.grayingout.bot.commands.implementations.SlowmodeCommand;
import me.grayingout.bot.commands.implementations.WarnCommand;
import me.grayingout.bot.commands.implementations.WarningsCommand;

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
    public static final WarnCommand WARN_COMMAND = new WarnCommand();

    /**
     * The {@code /warnings <member:user> [page:integer]} slash command
     */
    public static final WarningsCommand WARNINGS_COMMAND = new WarningsCommand();

    /**
     * The {@code /clear-warnings <member:user>} slash command
     */
    public static final ClearWarningsCommand CLEAR_WARNINGS_COMMAND = new ClearWarningsCommand();

    /**
     * The {@code /remove-warning <member:user> <id:integer>} slash command
     */
    public static final RemoveWarningCommand REMOVE_WARNING_COMMAND = new RemoveWarningCommand();
}
