package com.ecostream.anomaly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AnomalyApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnomalyApplication.class, args);
    }
}
