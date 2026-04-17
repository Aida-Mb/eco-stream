package com.ecostream.anomaly;

import com.ecostream.anomaly.dto.AnalysisResultDto;
import com.ecostream.anomaly.dto.EnergyReadingDto;
import com.ecostream.anomaly.repository.AnomalyAlertRepository;
import com.ecostream.anomaly.service.AnomalyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnomalyServiceTest {

    @Mock
    private AnomalyAlertRepository repository;

    @InjectMocks
    private AnomalyService anomalyService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(anomalyService, "highThreshold", 1000.0);
        ReflectionTestUtils.setField(anomalyService, "mediumThreshold", 500.0);
    }

    private EnergyReadingDto dto(double consumption) {
        return EnergyReadingDto.builder()
                .meterId("METER-001")
                .consumption(consumption)
                .timestamp("2024-06-01T10:00:00")
                .build();
    }

    @Test
    void testNormalConsumption() {
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        AnalysisResultDto result = anomalyService.analyze(dto(200.0));

        assertThat(result.getSeverityLevel()).isEqualTo("NORMAL");
        assertThat(result.isAnomalyDetected()).isFalse();
        verify(repository, never()).save(any());
    }

    @Test
    void testMediumAnomaly() {
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        AnalysisResultDto result = anomalyService.analyze(dto(750.0));

        assertThat(result.getSeverityLevel()).isEqualTo("MEDIUM");
        assertThat(result.isAnomalyDetected()).isTrue();
        verify(repository, times(1)).save(any());
    }

    @Test
    void testHighAnomaly() {
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        AnalysisResultDto result = anomalyService.analyze(dto(1500.0));

        assertThat(result.getSeverityLevel()).isEqualTo("HIGH");
        assertThat(result.isAnomalyDetected()).isTrue();
        verify(repository, times(1)).save(any());
    }

    @Test
    void testExactMediumThreshold() {
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        AnalysisResultDto result = anomalyService.analyze(dto(500.0));

        assertThat(result.getSeverityLevel()).isEqualTo("MEDIUM");
        assertThat(result.isAnomalyDetected()).isTrue();
    }

    @Test
    void testExactHighThreshold() {
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        AnalysisResultDto result = anomalyService.analyze(dto(1000.0));

        assertThat(result.getSeverityLevel()).isEqualTo("HIGH");
        assertThat(result.isAnomalyDetected()).isTrue();
    }
}
