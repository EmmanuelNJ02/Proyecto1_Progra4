package com.example.proyecto1.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StartupConfig {

    @Bean
    public CommandLineRunner ensureUploadDirectoryExists(AppProperties appProperties) {
        return args -> {
            Path uploadPath = Paths.get(appProperties.getUploadDir()).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
        };
    }
}
