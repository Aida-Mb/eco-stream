package com.ecostream.simulator.repository;

import com.ecostream.simulator.model.MeterConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeterConfigRepository extends JpaRepository<MeterConfig, Long> {

    List<MeterConfig> findAllByActiveTrue();

    boolean existsByMeterId(String meterId);
}
