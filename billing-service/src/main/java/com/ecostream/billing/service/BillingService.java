package com.ecostream.billing.service;

import com.ecostream.billing.dto.BillResponseDTO;
import com.ecostream.billing.dto.EnergyReadingDTO;
import com.ecostream.billing.exception.BillNotFoundException;
import com.ecostream.billing.model.Bill;
import com.ecostream.billing.model.Tariff;
import com.ecostream.billing.repository.BillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillingService {

    private static final Logger log = LoggerFactory.getLogger(BillingService.class);

    private final BillRepository billRepository;

    @Autowired
    public BillingService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Value("${tariff.peak-rate}")
    private double peakRate;

    @Value("${tariff.off-peak-rate}")
    private double offPeakRate;

    @Value("${tariff.peak-start-hour}")
    private int peakStartHour;

    @Value("${tariff.peak-end-hour}")
    private int peakEndHour;

    public BillResponseDTO calculateBill(EnergyReadingDTO reading) {
        log.info("Calcul facture pour compteur {} — {}kWh",
                reading.getMeterId(), reading.getConsumption());

        boolean isPeakHour = isInPeakHours(reading.getTimestamp());
        double rate = isPeakHour ? peakRate : offPeakRate;
        String tariffType = isPeakHour ? "HEURES_PLEINES" : "HEURES_CREUSES";

        double cost = reading.getConsumption() * rate;
        cost = Math.round(cost * 100.0) / 100.0;

        Bill bill = Bill.builder()
                .meterId(reading.getMeterId())
                .consumption(reading.getConsumption())
                .cost(cost)
                .rateApplied(rate)
                .tariffType(tariffType)
                .timestamp(reading.getTimestamp())
                .calculatedAt(LocalDateTime.now())
                .build();

        Bill saved = billRepository.save(bill);
        log.info("Facture sauvegardée : {} FCFA ({})", cost, tariffType);

        return toDTO(saved);
    }

    public List<BillResponseDTO> getBillsByMeterId(String meterId) {
        List<Bill> bills = billRepository.findByMeterIdOrderByTimestampDesc(meterId);

        if (bills.isEmpty()) {
            throw new BillNotFoundException(
                    "Aucune facture trouvée pour le compteur : " + meterId);
        }

        return bills.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Tariff getCurrentTariff() {
        return new Tariff(peakRate, offPeakRate, peakStartHour, peakEndHour);
    }

    private boolean isInPeakHours(LocalDateTime timestamp) {
        int hour = timestamp.getHour();
        return hour >= peakStartHour && hour < peakEndHour;
    }

    private BillResponseDTO toDTO(Bill bill) {
        return BillResponseDTO.builder()
                .id(bill.getId())
                .meterId(bill.getMeterId())
                .consumption(bill.getConsumption())
                .cost(bill.getCost())
                .rateApplied(bill.getRateApplied())
                .tariffType(bill.getTariffType())
                .timestamp(bill.getTimestamp())
                .calculatedAt(bill.getCalculatedAt())
                .build();
    }
}