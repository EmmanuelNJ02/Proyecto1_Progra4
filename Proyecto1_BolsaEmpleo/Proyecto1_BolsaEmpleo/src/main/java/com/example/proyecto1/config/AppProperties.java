package com.example.proyecto1.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * Directorio donde se almacenan los currículos.
     */
    private String uploadDir = System.getProperty("user.home") + "/bolsa_empleo_uploads/cv";

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
