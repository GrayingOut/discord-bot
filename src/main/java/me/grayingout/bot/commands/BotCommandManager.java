package me.grayingout.bot.commands;

import me.grayingout.bot.commands.implementations.AddLevelRoleCommand;
import me.grayingout.bot.commands.implementations.BulkDeleteCommand;
import me.grayingout.bot.commands.implementations.ClearWarningsCommand;
import me.grayingout.bot.commands.implementations.GetLevelRolesCommand;
import me.grayingout.bot.commands.implementations.HelloCommand;
import me.grayingout.bot.commands.implementations.LevelCommand;
import me.grayingout.bot.commands.implementations.LevelTopCommand;
import me.grayingout.bot.commands.implementations.RemoveLevelRoleCommand;
import me.grayingout.bot.commands.implementations.RemoveWarningCommand;
import me.grayingout.bot.commands.implementations.RulesCommand;
import me.grayingout.bot.commands.implementations.SetLevelCommand;
import me.grayingout.bot.commands.implementations.SetLoggingChannelCommand;
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

    /**
     * The {@code /set-logging-channel <channel:channel>} slash command
     */
    public static final SetLoggingChannelCommand SET_LOGGING_CHANNEL_COMMAND = new SetLoggingChannelCommand();

    /**
     * The {@code /level [member:user]} slash command
     */
    public static final LevelCommand LEVEL_COMMAND = new LevelCommand();

    /**
     * The {@code /set-level <member:user> <level:int>} command
     */
    public static final SetLevelCommand SET_LEVEL_COMMAND = new SetLevelCommand();

    /**
     * The {@code /level-top} command
     */
    public static final LevelTopCommand LEVEL_TOP_COMMAND = new LevelTopCommand();

    /**
     * The {@code /add-level-role <role:role> <level:int>} command
     */
    public static final AddLevelRoleCommand ADD_LEVEL_ROLE_COMMAND = new AddLevelRoleCommand();

    /**
     * The {@code /get-level-roles} command
     */
    public static final GetLevelRolesCommand GET_LEVEL_ROLES_COMMAND = new GetLevelRolesCommand();

    /**
     * The {@code /remove-level-role} command
     */
    public static final RemoveLevelRoleCommand REMOVE_LEVEL_ROLE_COMMAND = new RemoveLevelRoleCommand();
}
