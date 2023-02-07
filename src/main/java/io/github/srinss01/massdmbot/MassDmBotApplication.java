package io.github.srinss01.massdmbot;

import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

@SpringBootApplication
public class MassDmBotApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(MassDmBotApplication.class);

    public static void main(String[] args) throws IOException {
        val config = new File("config");
        if (!config.exists()) {
            val mkdir = config.mkdir();
            if (!mkdir) {
                LOGGER.error("Failed to create config directory");
                return;
            }
            LOGGER.info("Created config directory");
        }
        val properties = new File("config/application.yml");
        if (!properties.exists()) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the bot token: ");
            String token = scanner.nextLine();
            System.out.print("Enter the message to send: ");
            String message = scanner.nextLine();
            System.out.print("Enter the link to the server invite: ");
            String link = scanner.nextLine();
            System.out.print("Enter the channel id to log the messages: ");
            String logChannelId = scanner.nextLine();
            String content = "botToken: " + token + '\n' +
                             "message: " + message + '\n' +
                             "link: " + link + '\n' +
                             "logChannelId: " + logChannelId;
            Files.writeString(properties.toPath(), content);
        }
        SpringApplication.run(MassDmBotApplication.class, args);
    }

}
