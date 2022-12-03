package me.grayingout.bot.commands.implementations;

import me.grayingout.bot.commands.BotCommand;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command to make the bot say hello to you
 * or another user
 */
public class HelloCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands
            .slash("hello", "Say hello to the bot")
            .addOption(OptionType.USER, "user", "User to say hello to", false)
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User user = event.getUser();

        /* Check for optional option */
        if (event.getOption("user") != null) {
            user = event.getOption("user").getAsUser();
        }

        event.deferReply().queue();
        event.getHook().sendMessage(String.format(
            "Hello %s :wave:",
            user.getAsMention()
        )).queue();
    }
}
