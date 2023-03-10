package io.github.srinss01.massdmbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Events extends ListenerAdapter {
    @Value("${message}")
    private String message;
    @Value("${link}")
    private String link;

    @Value("${logChannelId}")
    private String logChannelId;

    private static final Logger LOGGER = LoggerFactory.getLogger(Events.class);
    @Override
    public void onReady(ReadyEvent event) {
        LOGGER.info("{} is ready!!", event.getJDA().getSelfUser().getName());
        LOGGER.info("Message: {}", message);
        LOGGER.info("Link: {}", link);
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
        Guild guild = Objects.requireNonNull(event.getGuild());
        int maxMembers = guild.getMemberCount();
        TextChannel logChannel = Objects.requireNonNull(guild).getTextChannelById(logChannelId);
        switch (event.getName()) {
            case "stop" -> {
                event.reply("Shutting down bot now!").setEphemeral(true).queue();
                event.getJDA().shutdown();
            }
            case "mass-dm" -> {
                event.reply("sent").setEphemeral(true).queue();
                AtomicInteger counter = new AtomicInteger(1);
                guild.getMembers().forEach(member -> {
                    if (counter.get() % 10 == 0) {
                        try {
                            Thread.sleep(10 * 60 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    User user = member.getUser();
                    if (!user.isBot() && user.getIdLong() != event.getJDA().getSelfUser().getIdLong()) {
                        CacheRestAction<PrivateChannel> privateChannelCacheRestAction = user.openPrivateChannel();
                        privateChannelCacheRestAction
                                .onSuccess(privateChannel ->
                                        privateChannel.sendMessageEmbeds(
                                            new EmbedBuilder().setDescription(message).build()
                                        ).addActionRow(Button.link(link, "Join server"))
                                        .queue(message -> {
                                            LOGGER.info("Sent message to {} [{}/{} users]", user.getName(), counter.get(), maxMembers);
                                            if (logChannel != null) {
                                                logChannel.sendMessageFormat("Sent message to %s `[%d/%d users]`", user.getAsTag(), counter.get(), maxMembers).queue();
                                            }
                                            counter.getAndIncrement();
                                        }))
                                .queue();
                    }
                });
            }
        }
    }
}
