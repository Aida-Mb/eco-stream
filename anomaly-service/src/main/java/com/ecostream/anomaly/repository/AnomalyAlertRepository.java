package com.ecostream.anomaly.repository;

import com.ecostream.anomaly.model.AnomalyAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnomalyAlertRepository extends JpaRepository<AnomalyAlert, Long> {

    /** Toutes les alertes d'un compteur, les plus récentes en premier */
    List<AnomalyAlert> findByMeterIdOrderByDetectedAtDesc(String meterId);

    /** Toutes les alertes d'un niveau de sévérité donné */
    List<AnomalyAlert> findBySeverityLevelOrderByDetectedAtDesc(String severityLevel);

    /** Nombre d'alertes par compteur */
    long countByMeterId(String meterId);
}
