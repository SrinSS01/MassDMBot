package io.github.srinss01.massdmbot;

import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
            try (
                    val url = MassDmBotApplication.class.getResourceAsStream("../../../../application.yml")
            ) {
                if (url == null) {
                    return;
                }
                Files.copy(url, properties.toPath());
                LOGGER.info("Created application.yml file");
            }
            return;
        }
        SpringApplication.run(MassDmBotApplication.class, args);
    }

}
