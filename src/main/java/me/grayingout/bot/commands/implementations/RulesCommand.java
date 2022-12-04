package me.grayingout.bot.commands.implementations;

import java.time.LocalDateTime;

import me.grayingout.bot.commands.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * A slash command used to send the rules embed in the
 * current channel - restricted to admins
 */
public class RulesCommand extends BotCommand {

    @Override
    public CommandData getCommandData() {
        return Commands
            .slash("rules", "Send the rules embed")
            .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
            .setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        /* Create the rules embed */
        MessageEmbed embed = new EmbedBuilder()
            .setTitle("Server Rules")
            .setDescription("The current server rules")
            .setThumbnail("https://grayingout.repl.co/static/imgs/pfp.png")
            .addField("**1. Follow Discord TOS and Community Guidelines**", "You are required to follow [Discord's ToS](https://discord.com/terms) and [Community Guidelines](https://discord.com/guidelines).", false)
            .addField("**2. Do not harass, threaten, or discriminate**", "Be respectful and tolerant of other members.", false)
            .addField("**3. No NSFW/18+ content**", "NSFW/18+ material has no place on this server.", false)
            .addField("**4. Do not spam**", "Do not spam or broadcast earr*pe in VCs.", false)
            .addField("**5. Do not advertise**", "Do not advertise on this server.", false)
            .addField("**6. Do not impersonate**", "Do not pretend to be someone of significance.", false)
            .addField("**7. Use common sense**", "This is not an exhaustive list. Use common sense and be respectful. Moderators have the final say.", false)
            .setFooter("Written by GrayingOut#1801", "https://grayingout.repl.co/static/imgs/pfp.png")
            .setTimestamp(LocalDateTime.now())
            .build();
        
        /* Send the embed and message to user */
        event.getChannel().sendMessageEmbeds(embed).queue();
        event.getHook().sendMessage("Rules have been sent").queue();
    }    
}
