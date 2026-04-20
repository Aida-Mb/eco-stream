# ⚡ Eco-Stream — Anomaly Detection Service
### Guide de test Postman

---

## 1. Health Check — le service est bien up ?

```
GET http://localhost:8084/health
```

Réponse attendue :
```
Anomaly Detection Service UP
```

---

## 2. Analyser une consommation NORMALE

```
POST http://localhost:8084/analyze
```

```json
{
  "meterId": "METER-001",
  "consumption": 2.0,
  "timestamp": "2024-06-01T10:00:00"
}
```

Réponse attendue :
```json
{
  "severityLevel": "NORMAL",
  "anomalyDetected": false,
  "message": "Consommation normale : 2.00 kWh"
}
```

> ✅ Aucune alerte créée en BDD — les mesures NORMAL ne sont pas persistées.

---

## 3. Détecter une anomalie MEDIUM

```
POST http://localhost:8084/analyze
```

```json
{
  "meterId": "METER-001",
  "consumption": 11.0,
  "timestamp": "2024-06-01T14:00:00"
}
```

Réponse attendue :
```json
{
  "severityLevel": "MEDIUM",
  "anomalyDetected": true,
  "message": "Consommation ÉLEVÉE : 11.00 kWh dépasse le seuil moyen de 500 kWh"
}
```

---

## 4. Détecter une anomalie HIGH

```
POST http://localhost:8084/analyze
```

```json
{
  "meterId": "METER-002",
  "consumption": 15.0,
  "timestamp": "2024-06-01T20:00:00"
}
```

Réponse attendue :
```json
{
  "severityLevel": "HIGH",
  "anomalyDetected": true,
  "message": "Consommation CRITIQUE : 15.00 kWh dépasse le seuil haut de 1000 kWh"
}
```

---

## 5. Récupérer toutes les alertes générées

```
GET http://localhost:8084/alerts
```

> Doit retourner les 2 alertes créées aux étapes 3 et 4 (NORMAL n'est pas persisté).

---

## 6. Filtrer les alertes par sévérité

```
GET http://localhost:8084/alerts?severity=HIGH
```

```
GET http://localhost:8084/alerts?severity=MEDIUM
```

---

## 7. Alertes d'un compteur spécifique

```
GET http://localhost:8084/alerts/meter/METER-001
```

> Retourne uniquement l'alerte MEDIUM créée pour ce compteur.

---

## 8. Récupérer une alerte par son ID

```
GET http://localhost:8084/alerts/1
```

---

## 9. Tester la validation (cas d'erreur)

POST `http://localhost:8084/analyze` sans `meterId` :

```json
{
  "consumption": 800.0,
  "timestamp": "2024-06-01T10:00:00"
}
```

Réponse attendue :
```json
{
  "status": 400,
  "message": "meterId: meterId is required"
}
```

---

## Résumé — Use Cases couverts

| Use Case | Couvert par |
|---|---|
| Analyser les données de consommation | Tests 2, 3, 4 |
| Détecter les anomalies (MEDIUM / HIGH / NORMAL) | Tests 2, 3, 4 |
| Générer et consulter les alertes | Tests 5, 6, 7, 8 |

---

## Seuils de détection

Configurables dans `application.yml` sans recompiler.

| Niveau | Condition                   | Persisté ? |
|---|-----------------------------|---|
| ✅ NORMAL | `consumption < 5 kWh`       | Non |
| 🟡 MEDIUM | `500 ≤ consumption < 9 kWh` | Oui |
| 🔴 HIGH | `consumption ≥ 14 kWh`      | Oui |