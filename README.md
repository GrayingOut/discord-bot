# Discord Bot

This is a simple bot application for discord that I am using to test features and experiment.
It is not meant to be used in an actual server, but feel free to use.

The bot requires the `ADMINISTRATOR` permission to function properly.

## Features

- Message cache
- Slash command support
- Hello command
- Levelling system w/ role rewards
- Warning system
- Deleted message logging
- Logging channel
- Bulk deletion
- Slowmode
- Welcome message w/ templating
- Rules command (used to print a pretty embed) - edit code to customise
- Persistence with sqlite

### Slash Commands

The bot has many slash commands - the amazing discord feature. Here is the list of them

|Command|Description|Permission|
|------------|---|---|
|`/add-level-role <role> <level>`| Adds a role as a reward for a level | `MANAGE_ROLES` |
|`/remove-level-role <role>` | Removes a role from any reward level | `MANAGE_ROLES` |
|`/get-level-roles`| Gets a list of reward roles |  |
|`/bulk-delete <count>`| Purges an amount of messages | `MESSAGE_MANAGE` |
|`/clear-warnings <member>`| Clears a members warnings | `ADMINISTRATOR` |
|`/remove-warning <member> <warning_id>`| Removes a specific warning from a member | `ADMINISTRATOR` |
|`/warn <member> [reason]`| Warns a member with an optional reason | `MODERATE_MEMBERS` |
|`/warnings <member>`| View the warnings of a member | `MODERATE_MEMBERS` |
|`/hello [member]`| Say hello to the bot | |
|`/level [member]`| Get your own or another member's level | |
|`/level-top`| View the top 5 levels | |
|`/set-level <member> <level>`| Set a member's level | `ADMINISTRATOR` |
|`/reset-level <member>`| Reset a member's level | `ADMINISTRATOR` |
|`/rules`| Pretty print an embed of the rules | `ADMINISTRATOR` |
|`/set-logging-channel <channel>`| Set the bot logging channel | `MANAGE_SERVER` |
|`/slowmode <seconds>`| Set the slowmode of a channel | `MANAGE_CHANNEL` |
|`/welcome-message set-channel <channel>`| Set the welcome message channel | `MANAGE_SERVER` |
|`/welcome-message remove-channel`| Remove the welcome message channel | `MANAGE_SERVER` |
|`/welcome-message set-message <message>`| Set the welcome message | `MANAGE_SERVER` |
|`/welcome-message show-config`| Show the current welcome message config | `MANAGE_SERVER` |

## Future Features (maybe)

- Saving the message cache to a database
- Use one database file (why i haven't already is beyond me)
- External config file (something like `bot.config.json`)
- Polls
- Giveaways
- Role messages - messages that give you roles
- More logging options
- More moderation tools
- [Playing audio](https://github.com/GrayingOut/discord-bot/tree/audio-player) - currently doesn't work

## Running yourself

Run using the exec-maven-plugin build goal

```bash
mvn clean verify exec:java
```

## Found a bug/issue

<img width="300" src="https://grayingout.repl.co/static/donttouchmygarbage.png" />
