package com.podcast.api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PodcastApplication {

    public static void main(String[] args) {
        loadDotenv();
        SpringApplication.run(PodcastApplication.class, args);
    }

    private static void loadDotenv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
            dotenv.entries().forEach(entry -> {
                if (System.getenv(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });
        } catch (Exception e) {
            System.out.println("Файл .env не найден — используются системные переменные окружения");
        }
    }
}