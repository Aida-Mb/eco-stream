# Simulator Service — Eco-Stream

Génère des données de consommation énergétique simulées pour N compteurs intelligents
et les envoie en continu à l'**Ingestion Service**.

## Démarrage rapide

```bash
# Depuis le dossier simulator-service/
mvn spring-boot:run
```

Le service démarre sur **http://localhost:8081**

> ⚠️ L'Ingestion Service doit tourner sur :8082, sinon les envois échoueront
> (le simulateur logge un warning et continue sans s'arrêter).

---

## Endpoints

| Méthode | Route          | Description                          |
|---------|----------------|--------------------------------------|
| POST    | `/start`       | Démarre la simulation                |
| POST    | `/stop`        | Arrête la simulation                 |
| GET     | `/status`      | État courant                         |
| GET     | `/health`      | Health check simple                  |
| GET     | `/actuator/health` | Health check détaillé (Spring)   |

---

## Exemples cURL

### Démarrer la simulation (5 compteurs, 1 relevé/2s)
```bash
curl -X POST http://localhost:8081/start \
  -H "Content-Type: application/json" \
  -d '{"meterCount": 5, "intervalMs": 2000}'
```

### Vérifier l'état
```bash
curl http://localhost:8081/status
```

### Arrêter
```bash
curl -X POST http://localhost:8081/stop
```

---

## Payload envoyé à l'Ingestion Service

Chaque compteur envoie ce JSON toutes les `intervalMs` millisecondes :

```json
{
  "meterId": "house-3",
  "consumption": 4.72,
  "timestamp": "2025-04-15T14:30:00.123Z"
}
```

**10% des relevés sont volontairement anormaux** (consommation ×4 à ×6)
pour tester l'Anomaly Detection Service.

---

## Structure du projet

```
simulator-service/
├── src/main/java/com/ecostream/simulator/
│   ├── SimulatorApplication.java       ← point d'entrée
│   ├── config/
│   │   └── AppConfig.java              ← bean RestTemplate
│   ├── controller/
│   │   ├── SimulatorController.java    ← REST endpoints
│   │   └── GlobalExceptionHandler.java ← gestion erreurs
│   ├── dto/
│   │   ├── StartRequest.java           ← body POST /start
│   │   ├── EnergyReadingDto.java       ← payload → Ingestion
│   │   └── SimulatorStatusDto.java     ← réponse /status
│   ├── model/
│   │   └── MeterConfig.java            ← entité JPA
│   ├── repository/
│   │   └── MeterConfigRepository.java
│   └── service/
│       └── SimulatorService.java       ← logique principale
└── src/main/resources/
    └── application.properties
```

---

## Variables de configuration

| Propriété                        | Défaut  | Description                          |
|----------------------------------|---------|--------------------------------------|
| `server.port`                    | 8081    | Port du service                      |
| `ingestion.service.url`          | http://localhost:8082 | URL de l'Ingestion  |
| `simulator.default.meter-count`  | 5       | Nb de compteurs par défaut           |
| `simulator.default.interval-ms`  | 2000    | Intervalle par défaut (ms)           |
| `simulator.min-consumption`      | 0.5     | Consommation minimale (kWh)          |
| `simulator.max-consumption`      | 15.0    | Consommation maximale normale (kWh)  |
