# ⚡ Eco-Stream
Système de gestion de consommation énergétique en temps réel — Architecture Microservices

## Démarrage (ordre obligatoire)
```bash
cd config-server  && mvn spring-boot:run
cd eureka-server  && mvn spring-boot:run
cd api-gateway    && mvn spring-boot:run
cd simulator-service && mvn spring-boot:run
cd ingestion-service && mvn spring-boot:run
cd billing-service   && mvn spring-boot:run
cd anomaly-service   && mvn spring-boot:run
```

## URLs
- Eureka   : http://localhost:8761
- Config   : http://localhost:8888
- Gateway  : http://localhost:8080
