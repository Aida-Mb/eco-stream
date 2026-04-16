package com.ecostream.ingestion.repository;

import com.ecostream.ingestion.model.EnergyReading;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnergyReadingRepository extends JpaRepository<EnergyReading, Long> {

    Page<EnergyReading> findAllByOrderByReceivedAtDesc(Pageable pageable);
}