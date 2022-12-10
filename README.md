# Discord Bot

This is a simple bot application for discord that I am using to test features and experiment.
It is not meant to be used in an actual server, but feel free to use.

The bot requires the `ADMINISTRATOR` permission to function properly.

## Table of Contents
1. [Features](#features)
    1. [Overview](#overview)
    2. [Slash Commands](#slash-commands)
    3. [Logging](#logging)
2. [Future Features](#future-features-maybe)
3. [Running yourself](#running-yourself)
4. [Found a bug/issue](#found-a-bugissue)

## Features

### Overview

- Message cache
- Slash command support
- Hello command
- Levelling system w/ role rewards
- Warning system
- Logging System
- Bulk deletion
- Slowmode
- Welcome message w/ templating
- Rules command (used to print a pretty embed) - edit code to customise
- Playing audio
- Persistence with sqlite

### Slash Commands

The bot has many slash commands - the amazing discord feature. Here is the list of them

|Command|Description|Permission|
|---|---|---|
|`/level-roles add <role> <level>`| Adds a role as a reward for a level | `MANAGE_ROLES`, `MANAGE_SERVER` |
|`/level-roles remove <role>` | Removes a role from any reward level | `MANAGE_ROLES`, `MANAGE_SERVER` |
|`/level-roles list`| Gets a list of reward roles | `MANAGE_ROLES`, `MANAGE_SERVER` |
|`/levels get [member]`| Get your own or another member's level | |
|`/levels top`| View the top 5 levels | |
|`/levels set <member> <level>`| Set a member's level | `ADMINISTRATOR` |
|`/warnings add <member> [reason]`| Give a member a warning | `MODERATE_MEMBERS` |
|`/warnings remove <member> <id>`| Remove a warning from a member | `ADMINISTRATOR` |
|`/warnings list <member>`| View the warnings of a member | `MODERATE_MEMBERS` |
|`/warnings clear <member>`| Clear a member's warnings | `ADMINISTRATOR` |
|`/logging set-channel <channel>`| Set the bot logging channel | `MANAGE_SERVER` |
|`/logging remove-channel <channel>`| Remove the bot logging channel | `MANAGE_SERVER` |
|`/logging enable-logging <type>`| Enable a type of logging | `MANAGE_SERVER` |
|`/logging disable-logging <type>`| Disable a type of logging | `MANAGE_SERVER` |
|`/logging show-config`| Show the current logging setup | `MANAGE_SERVER` |
|`/welcome-message set-channel <channel>`| Set the welcome message channel | `MANAGE_SERVER` |
|`/welcome-message remove-channel`| Remove the welcome message channel | `MANAGE_SERVER` |
|`/welcome-message set-message <message>`| Set the welcome message | `MANAGE_SERVER` |
|`/welcome-message show-config`| Show the current welcome message config | `MANAGE_SERVER` |
|`/bulk-delete <count>`| Purges an amount of messages | `MESSAGE_MANAGE` |
|`/hello [member]`| Say hello to the bot | |
|`/rules`| Pretty print an embed of the rules | `ADMINISTRATOR` |
|`/slowmode <seconds>`| Set the slowmode of a channel | `MANAGE_CHANNEL` |
|`/join`| Joins the member's audio channel | |
|`/leave`| Leaves its current audio channel | |
|`/play <url>`| Plays an audio from a URL | |
|`/search <search>`| Searches YouTube for an audio | |
|`/stop`| Stops the current playing audio and clears the audio queue | |

### Logging

These are the types of logging the bot currently supports

|Logging Type|Events Logged|
|---|---|
| MESSAGE_DELETION_LOGGING | `MESSAGE_DELETE` |
| ROLE_LOGGING | `ROLE_CREATE`, `ROLE_DELETE`, `ROLE_UPDATE_NAME`, `ROLE_UPDATE_PERMISSIONS` |
| CHANNEL_LOGGING | `CHANNEL_CREATE`, `CHANNEL_DELETE`, `CHANNEL_UPDATE_NAME` |

## Future Features (maybe)

- Saving the message cache to a database
- Use one database file (why i haven't already is beyond me)
- External config file (something like `bot.config.json`)
- Polls
- Giveaways
- Role messages - messages that give you roles
- More moderation tools
- [Playing audio](https://github.com/GrayingOut/discord-bot/tree/audio-player) - in development

## Running yourself

Run using the exec-maven-plugin build goal

```bash
mvn clean verify exec:java
```

## Found a bug/issue

<img width="300" src="https://grayingout.repl.co/static/donttouchmygarbage.png" />
