package com.ecostream.simulator;

import com.ecostream.simulator.dto.EnergyReadingDto;
import com.ecostream.simulator.dto.SimulatorStatusDto;
import com.ecostream.simulator.dto.StartRequest;
import com.ecostream.simulator.model.MeterConfig;
import com.ecostream.simulator.repository.MeterConfigRepository;
import com.ecostream.simulator.service.SimulatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulatorServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MeterConfigRepository meterConfigRepository;

    @InjectMocks
    private SimulatorService simulatorService;

    @BeforeEach
    void setUp() {
        // Prépare un compteur fictif retourné par le repository
        MeterConfig fakeMeter = MeterConfig.builder()
                .id(1L)
                .meterId("house-1")
                .intervalMs(2000)
                .active(true)
                .build();

        when(meterConfigRepository.findAllByActiveTrue()).thenReturn(List.of(fakeMeter));
        when(meterConfigRepository.findAll()).thenReturn(Collections.emptyList());
        when(meterConfigRepository.existsByMeterId("house-1")).thenReturn(false);
        when(meterConfigRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void getStatus_whenNotStarted_returnsStopped() {
        SimulatorStatusDto status = simulatorService.getStatus();
        assertThat(status.getStatus()).isEqualTo("STOPPED");
        assertThat(status.getMeterCount()).isZero();
    }

    @Test
    void stop_whenAlreadyStopped_returnsStoppedStatus() {
        SimulatorStatusDto status = simulatorService.stop();
        assertThat(status.getStatus()).isEqualTo("STOPPED");
    }

    @Test
    void start_setsRunningStatusWithCorrectMeterCount() throws InterruptedException {
        StartRequest request = new StartRequest();
        request.setMeterCount(1);
        request.setIntervalMs(5000);

        SimulatorStatusDto status = simulatorService.start(request);

        assertThat(status.getStatus()).isEqualTo("RUNNING");
        assertThat(status.getMeterCount()).isEqualTo(1);
        assertThat(status.getIntervalMs()).isEqualTo(5000);

        // Nettoyage
        simulatorService.stop();
    }
}
