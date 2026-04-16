package com.ecostream.simulator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /**
     * RestTemplate utilisé pour appeler l'Ingestion Service.
     * En production, on pourrait le remplacer par WebClient (réactif)
     * ou ajouter un LoadBalancerClient pour la découverte Eureka.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
