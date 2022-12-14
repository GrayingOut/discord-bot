# Discord Bot

This is a simple multi-utility discord bot that I am developing to learn more about JDA and Java.
This has not been desgined for server use, but you are free to use it.

The bot requires the `ADMINISTRATOR` permission to function properly.

## Table of Contents
1. [Features](#features)
    1. [Overview](#overview)
    2. [Slash Commands](#slash-commands)
        1. [Levelling](#level-commands)
        2. [Warnings](#warning-commands)
        3. [Logging](#logging-commands)
        4. [Welcome Message](#welcome-message-commands)
        5. [Audio](#audio-commands)
        6. [Uncategorised](#uncategorised-commands)
    3. [Logging](#logging)
2. [Future Features](#future-features)
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
- Playing audio w/ DJ role
- Persistence with sqlite

### Slash Commands

The bot has many slash commands - the amazing discord feature. Here is the list of them

#### Level Commands

|Command|Description|Permission|
|---|---|---|
|`/level-roles add <role> <level>`| Adds a role as a reward for a level | `MANAGE_ROLES`, `MANAGE_SERVER` |
|`/level-roles remove <role>` | Removes a role from any reward level | `MANAGE_ROLES`, `MANAGE_SERVER` |
|`/level-roles list`| Gets a list of reward roles | `MANAGE_ROLES`, `MANAGE_SERVER` |
|`/levels get [member]`| Get your own or another member's level | |
|`/levels top`| View the top 5 levels | |
|`/levels set <member> <level>`| Set a member's level | `ADMINISTRATOR` |

#### Warning Commands

|Command|Description|Permission|
|---|---|---|
|`/warnings add <member> [reason]`| Give a member a warning | `MODERATE_MEMBERS` |
|`/warnings remove <member> <id>`| Remove a warning from a member | `ADMINISTRATOR` |
|`/warnings list <member>`| View the warnings of a member | `MODERATE_MEMBERS` |
|`/warnings clear <member>`| Clear a member's warnings | `ADMINISTRATOR` |

#### Logging Commands

|Command|Description|Permission|
|---|---|---|
|`/logging set-channel <channel>`| Set the bot logging channel | `MANAGE_SERVER` |
|`/logging remove-channel <channel>`| Remove the bot logging channel | `MANAGE_SERVER` |
|`/logging enable-logging <type>`| Enable a type of logging | `MANAGE_SERVER` |
|`/logging disable-logging <type>`| Disable a type of logging | `MANAGE_SERVER` |
|`/logging show-config`| Show the current logging setup | `MANAGE_SERVER` |

#### Welcome Message Commands

|Command|Description|Permission|
|---|---|---|
|`/welcome-message set-channel <channel>`| Set the welcome message channel | `MANAGE_SERVER` |
|`/welcome-message remove-channel`| Remove the welcome message channel | `MANAGE_SERVER` |
|`/welcome-message set-message <message>`| Set the welcome message | `MANAGE_SERVER` |
|`/welcome-message show-config`| Show the current welcome message config | `MANAGE_SERVER` |

#### Audio Commands

|Command|Description|Permission|
|---|---|---|
|`/join`| Joins the member's audio channel | |
|`/leave`| Leaves its current audio channel | |
|`/play <url>`| Plays an audio from a URL | |
|`/search <search>`| Searches YouTube for an audio | |
|`/stop`| Stops the current playing audio and clears the audio queue | `DJ_USER`\* |
|`/queue`| View an interactable queue list message | `DJ_USER`\* - for the clear queue action |
|`/playing`| View the currently playing audio track | |
|`/skip`| Vote to skip the current track | |
|`/loop <enabled>`| Enables looping the next/currently playing audio | `DJ_USER` |
|`/dj-role set <role>`| Set the DJ role | `MANAGE_ROLES`, `MANAGE_SERVER` |
|`/dj-role get`| View the DJ role | `MANAGE_ROLES`, `MANAGE_SERVER` |
|`/dj-role remove`| Remove the DJ role | `MANAGE_ROLES`, `MANAGE_SERVER` |

#### Uncategorised Commands

|Command|Description|Permission|
|---|---|---|
|`/bulk-delete <count>`| Purges an amount of messages | `MESSAGE_MANAGE` |
|`/hello [member]`| Say hello to the bot | |
|`/rules`| Pretty print an embed of the rules | `ADMINISTRATOR` |
|`/slowmode <seconds>`| Set the slowmode of a channel | `MANAGE_CHANNEL` |

\*`DJ_USER` refers to a user who is either the owner, has `MANAGE_SERVER`, has `ADMINISTRATOR`, or has the DJ role

### Logging

These are the types of logging the bot currently supports

|Logging Type|Events Logged|
|---|---|
| MESSAGE_DELETION_LOGGING | `MESSAGE_DELETE` |
| ROLE_LOGGING | `ROLE_CREATE`, `ROLE_DELETE`, `ROLE_UPDATE_NAME`, `ROLE_UPDATE_PERMISSIONS` |
| CHANNEL_LOGGING | `CHANNEL_CREATE`, `CHANNEL_DELETE`, `CHANNEL_UPDATE_NAME` |

## Future Features

- Saving the message cache to a database
- Use one database file (why i haven't already is beyond me)
- External config file (something like `bot.config.json`)
- Polls
- Giveaways
- Role messages - messages that give you roles
- More moderation tools
- More audio commands (/pause, /resume)

## Running yourself

Run using the exec-maven-plugin build goal

```bash
mvn clean verify exec:java
```

## Found a bug/issue

This is a personal project, but I am happy to fix any issues you find.

I am not the best programmer, and I am using this as a learning experience.

<img width="200" src="https://grayingout.repl.co/static/donttouchmygarbage.png" />

