package hr.parkulator.parkulator_backend;

import hr.parkulator.parkulator_backend.entities.LocationLog;
import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.repositories.LocationLogRepository;
import hr.parkulator.parkulator_backend.repositories.ParkingRepository;
import hr.parkulator.parkulator_backend.services.Telemetry.ParkingCapacityEstimationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@ExtendWith(MockitoExtension.class)
public class ParkingCapacityEstimationServiceTest {
    @Mock
    private LocationLogRepository locationLogRepository;
    
    @Mock
    private ParkingRepository parkingRepository;

    @InjectMocks
    private ParkingCapacityEstimationService parkingCapacityEstimationService;

    @Test
    void shouldDoNotginhWhenNoLocationLogs() {
        // Test verifies that no updates occur when there are no LocationLog entries in the period.
        when(locationLogRepository.findByEventTimestampBetweenOrderByEventTimestampAsc(any(), any()))
        .thenReturn(List.of());

        parkingCapacityEstimationService.estimateParkingCapacity();

        verify(locationLogRepository, times(1))
            .findByEventTimestampBetweenOrderByEventTimestampAsc(any(), any());

        verify(parkingRepository, never()).save(any());
    }       

    @Test
    void shouldSkipLogWhenParkingIsNull() {
        // Test verifies that the log is skipped when the parking field in the LocationLog is null.
        LocationLog log = new LocationLog();
        log.setParking(null);
        log.setEventTimestamp(Instant.now());

        when(locationLogRepository.findByEventTimestampBetweenOrderByEventTimestampAsc(any(), any()))
                .thenReturn(List.of(log));

        parkingCapacityEstimationService.estimateParkingCapacity();

        verify(parkingRepository, never()).save(any());
    }

    @Test
    void shouldSkipLogWhenParkingIsLive() {
        // Test verifies that the log is skipped when the parking is marked as live (capacity not updated).
        Parking parking = new Parking();
        parking.setLive(true);

        LocationLog log = new LocationLog();
        log.setParking(parking);
        log.setEventTimestamp(Instant.now());
        log.setParkingLikelihood(0.8);

        when(locationLogRepository.findByEventTimestampBetweenOrderByEventTimestampAsc(any(), any()))
                .thenReturn(List.of(log));

        parkingCapacityEstimationService.estimateParkingCapacity();

        verify(parkingRepository, never()).save(any());
    }

    @Test
    void shouldSkipLogWhenCategoryIsNull() {
        // Test verifies that the log is skipped when the category in the LocationLog is null.
        Parking parking = new Parking();
        parking.setLive(false);

        LocationLog log = new LocationLog();
        log.setParking(parking);
        log.setCategory(null);
        log.setParkingLikelihood(0.8);
        log.setEventTimestamp(Instant.now());

        when(locationLogRepository.findByEventTimestampBetweenOrderByEventTimestampAsc(any(), any()))
                .thenReturn(List.of(log));

        parkingCapacityEstimationService.estimateParkingCapacity();

        verify(parkingRepository, never()).save(any());
    }

    @Test
    void shouldSkipLogWhenParkingLikelihoodIsNull() {
        // Test verifies that the log is skipped when parkingLikelihood is null (unknown likelihood).
        Parking parking = new Parking();
        parking.setLive(false);

        LocationLog log = new LocationLog();
        log.setParking(parking);
        log.setParkingLikelihood(null);
        log.setEventTimestamp(Instant.now());

        when(locationLogRepository.findByEventTimestampBetweenOrderByEventTimestampAsc(any(), any()))
                .thenReturn(List.of(log));

        parkingCapacityEstimationService.estimateParkingCapacity();

        verify(parkingRepository, never()).save(any());
    }

    
}