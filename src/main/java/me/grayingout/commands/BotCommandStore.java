package me.grayingout.commands;

import me.grayingout.commands.implementations.BulkDeleteCommand;
import me.grayingout.commands.implementations.ClearWarningsCommand;
import me.grayingout.commands.implementations.HelloCommand;
import me.grayingout.commands.implementations.RemoveWarningCommand;
import me.grayingout.commands.implementations.RulesCommand;
import me.grayingout.commands.implementations.SlowmodeCommand;
import me.grayingout.commands.implementations.WarnCommand;
import me.grayingout.commands.implementations.WarningsCommand;

/**
 * Stores references to all bot commands
 */
public final class BotCommandStore {
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
