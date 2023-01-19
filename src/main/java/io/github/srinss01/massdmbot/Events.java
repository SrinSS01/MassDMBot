package io.github.srinss01.massdmbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Events extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Events.class);
    @Override
    public void onReady(ReadyEvent event) {
        LOGGER.info("{} is ready!!", event.getJDA().getSelfUser().getName());
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        Guild guild = event.getGuild();
        guild.updateCommands().addCommands(
                Commands.slash("stop", "stop the bot"),
                Commands.slash("mass-dm", "send the mass DM message")
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "stop" -> {
                event.reply("Shutting down bot now!").setEphemeral(true).queue();
                event.getJDA().shutdown();
            }
            case "mass-dm" -> {
                Guild guild = event.getGuild();
                if (guild == null) {
                    event.deferReply().queue();
                    return;
                }
                event.reply("sent").setEphemeral(true).queue();
                guild.getMembers().forEach(member -> {
                    User user = member.getUser();
                    if (!user.isBot() && user.getIdLong() != event.getJDA().getSelfUser().getIdLong()) {
                        user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessageEmbeds(
                                new EmbedBuilder().setDescription("""
                                        Buna ziua . Doresc sa te invit pe noul server de discord OG Romania al lui Vladutz si Bogdiz
                                        Din cauza unor inconveniente , unul dintre membrii highstaffului din trecut, a banat majoritatea comunitatii de pe serverul de discord. Toti cei care ati fost in staff puteti reintra si va puteti primii gradele inapoi.
                                        Multumesc de atentie . Iti uram o zi fericita
                                        """).build()
                        ).addActionRow(Button.link("https://discord.gg/ogromania", "Join server")).queue(message -> LOGGER.info("Sent message to {}", user.getName())));
                    }
                });
            }
        }
    }
}
