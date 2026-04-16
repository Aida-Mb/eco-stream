package com.ecostream.billing.controller;

import com.ecostream.billing.dto.BillResponseDTO;
import com.ecostream.billing.dto.EnergyReadingDTO;
import com.ecostream.billing.model.Tariff;
import com.ecostream.billing.service.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    /**
     * POST /bills/calculate
     * Appelé par Ingestion Service après validation d'une mesure.
     * Calcule le coût et retourne la facture.
     */
    @PostMapping("/bills/calculate")
    public ResponseEntity<BillResponseDTO> calculateBill(
            @Valid @RequestBody EnergyReadingDTO reading) {

        log.info("Reçu demande de calcul : compteur={}", reading.getMeterId());
        BillResponseDTO response = billingService.calculateBill(reading);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /bills/{meterId}
     * Retourne l'historique complet de facturation d'un compteur.
     * Exemple : GET /bills/METER-001
     */
    @GetMapping("/bills/{meterId}")
    public ResponseEntity<List<BillResponseDTO>> getBillsByMeter(
            @PathVariable String meterId) {

        List<BillResponseDTO> bills = billingService.getBillsByMeterId(meterId);
        return ResponseEntity.ok(bills);
    }

    /**
     * GET /tariff
     * Retourne les tarifs actuels (utile pour le frontend ou les audits).
     */
    @GetMapping("/tariff")
    public ResponseEntity<Tariff> getCurrentTariff() {
        return ResponseEntity.ok(billingService.getCurrentTariff());
    }

    /**
     * GET /health
     * Health check standard — le Gateway et Eureka l'utilisent.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Billing Service UP");
    }
}