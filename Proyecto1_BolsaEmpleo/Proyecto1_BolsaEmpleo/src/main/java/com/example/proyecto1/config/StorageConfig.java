package com.example.proyecto1.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration

@EnableConfigurationProperties(AppProperties.class)
public class StorageConfig {

}
