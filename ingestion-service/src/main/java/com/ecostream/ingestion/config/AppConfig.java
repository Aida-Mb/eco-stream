package com.ecostream.ingestion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /**
     * RestTemplate utilisé pour appeler le Billing Service et l'Anomaly Service.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}