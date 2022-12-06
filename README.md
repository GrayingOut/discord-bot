# Discord Bot

This is a simple bot application for discord that I am using to test features and experiment.
It is not meant to be used in an actual server, but feel free to use.

The bot requires the `ADMINISTRATOR` permission to function properly.

## Features
- Message cache
- Slash command support
- Hello command
- Simple levelling system (guild specific)
    - 1xp per message (ignores non-guild messages and bot messages)
    - View a member's level
    - Reset a member's level
    - Set a member's level
    - Level advancement message
- Warning system (guild specific)
    - Warn member
    - Remove warning
    - Clear warnings
    - List warnings
- Deleted message logging
    - Logs the author, channel, message content, embeds and attachments
    - Also has an unknown deleted message embed for a message that was
      not in the message cache
- Logging channel
    - Use `/set-logging-channel <channel:channel>` to set the channel to use
- Bulk deletion
- Slowmode
- Rules command (used to print a pretty embed) - edit code to customise
- Persistence with sqlite

## Working On
- Levelling system
    - [x] View level
    - [x] Set member level
    - [x] Reset member level
    - [x] Level advancement message
    - [ ] Guild levels top (top 10 levels)
    - [ ] Role level rewards

## Future Features (maybe)
- [ ] Welcome message (which allows formatting)
- [ ] Saving the message cache to a database
- [ ] Use one database file (why i haven't already is beyond me)
- [ ] External config file (something like `bot.config.json`)
- [ ] Polls
- [ ] Giveaways
- [ ] Role messages - messages that give you roles
- [ ] More logging options
- [ ] More moderation tools

## Running yourself

Run using the exec-maven-plugin build goal

```bash
mvn clean verify exec:java
```

## Found a bug/issue

<img width="300" src="https://grayingout.repl.co/static/donttouchmygarbage.png" />
