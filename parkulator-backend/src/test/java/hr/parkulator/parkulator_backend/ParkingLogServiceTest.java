package hr.parkulator.parkulator_backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import hr.parkulator.parkulator_backend.dto.LocationLogDTO;
import hr.parkulator.parkulator_backend.entities.LocationLog;
import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.shared.ParkingMovementCategory;
import hr.parkulator.parkulator_backend.repositories.LocationLogRepository;
import hr.parkulator.parkulator_backend.repositories.ParkingRepository;
import hr.parkulator.parkulator_backend.services.Telemetry.GeoService;
import hr.parkulator.parkulator_backend.services.Telemetry.LocationLogService;


@ExtendWith(MockitoExtension.class)
public class ParkingLogServiceTest {
    @Mock
    private ParkingRepository parkingRepository;

    @Mock
    private GeoService geoService;

    @Mock
    private LocationLogRepository locationLogRepository;

    @InjectMocks
    private LocationLogService locationLogService;

    private LocationLogDTO createValidDto() {
    // Creates a valid LocationLogDTO used by the tests.
    LocationLogDTO dto = new LocationLogDTO();
        dto.setLatitude1(45.3271);
        dto.setLongitude1(14.4422);
        dto.setLatitude2(45.3275);
        dto.setLongitude2(14.4430);
        dto.setTimestamp1(Instant.parse("2026-05-31T10:00:10Z"));
        dto.setTimestamp2(Instant.parse("2026-05-31T10:00:15Z"));
        dto.setAccuracy(10.0);
        return dto;
    }

    @Test
    void shouldDoNothingWhenTimestamp2IsBeforeTimestamp1() {
        // Test verifies that the service takes no action when timestamp2 is before timestamp1.
        LocationLogDTO dto = new LocationLogDTO();
        dto.setLatitude1(45.3271);
        dto.setLongitude1(14.4422);
        dto.setLatitude2(45.3275);
        dto.setLongitude2(14.4430);
        dto.setTimestamp1(Instant.parse("2026-05-31T10:00:10Z"));
        dto.setTimestamp2(Instant.parse("2026-05-31T10:00:05Z"));
        dto.setAccuracy(10.0);

        locationLogService.filterLocationData(dto);

        verify(locationLogRepository, never()).save(any());
        verifyNoInteractions(parkingRepository);
    }

        private Parking createParking() {
        // Creates a sample Parking entity for tests.
        Parking parking = new Parking();
        parking.setLatitude(45.3273);
        parking.setLongitude(14.4428);
        return parking;
    }

    private void mockCommonValidFlow(
        LocationLogDTO dto,
        Parking parking,
        double speedKmh,
        double distance1,
        double distance2,
        double userHeading,
        double parkingBearing
    ) {
        // Stubs common GeoService/ParkingRepository behavior to simulate a valid flow.
        when(geoService.isInRijekaScope(anyDouble(), anyDouble())).thenReturn(true);

        when(parkingRepository.filterAll(any(), any(), anyDouble(), anyDouble()))
                .thenReturn(List.of(parking));

        when(geoService.calculateSpeedKmh(
                dto))
                .thenReturn(speedKmh);

        when(geoService.calculateHeading(
                dto.getLatitude1(), dto.getLongitude1(),
                dto.getLatitude2(), dto.getLongitude2()))
                .thenReturn(userHeading);

        when(geoService.calculateHeading(
                dto.getLatitude2(), dto.getLongitude2(),
                parking.getLatitude(), parking.getLongitude()))
                .thenReturn(parkingBearing);

        when(geoService.calculateDistanceMeters(
                dto.getLatitude1(), dto.getLongitude1(),
                parking.getLatitude(), parking.getLongitude()))
                .thenReturn(distance1);

        when(geoService.calculateDistanceMeters(
                dto.getLatitude2(), dto.getLongitude2(),
                parking.getLatitude(), parking.getLongitude()))
                .thenReturn(distance2);
    }

    @Test
    void shouldDoNothingWhenTimeDifferenceIsLessThan3Seconds() {
        // Test verifies that no save occurs when time difference is less than 3 seconds.
        LocationLogDTO dto = new LocationLogDTO();
        dto.setLatitude1(45.3271);
        dto.setLongitude1(14.4422);
        dto.setLatitude2(45.3275);
        dto.setLongitude2(14.4430);
        dto.setTimestamp1(Instant.parse("2026-05-31T10:00:10Z"));
        dto.setTimestamp2(Instant.parse("2026-05-31T10:00:12Z"));
        dto.setAccuracy(10.0);

        locationLogService.filterLocationData(dto);

        verify(locationLogRepository, never()).save(any());
        verifyNoInteractions(parkingRepository, geoService);
    }

    @Test
    void shouldDoNothingWhenTimeDifferenceIsGreaterThan15Seconds() {
        // Test verifies that no save occurs when time difference is greater than 15 seconds.
        LocationLogDTO dto = new LocationLogDTO();
        dto.setLatitude1(45.3271);
        dto.setLongitude1(14.4422);
        dto.setLatitude2(45.3275);
        dto.setLongitude2(14.4430);
        dto.setTimestamp1(Instant.parse("2026-05-31T10:00:10Z"));
        dto.setTimestamp2(Instant.parse("2026-05-31T10:00:30Z"));
        dto.setAccuracy(10.0);

        locationLogService.filterLocationData(dto);

        verify(locationLogRepository, never()).save(any());
        verifyNoInteractions(parkingRepository, geoService);
    }

    @Test
    void shouldDoNothingWhenAccuracyIsGreaterThan30() {
        // Test verifies that no save occurs when accuracy is greater than 30.
        LocationLogDTO dto = new LocationLogDTO();
        dto.setLatitude1(45.3271);
        dto.setLongitude1(14.4422);
        dto.setLatitude2(45.3275);
        dto.setLongitude2(14.4430);
        dto.setTimestamp1(Instant.parse("2026-05-31T10:00:10Z"));
        dto.setTimestamp2(Instant.parse("2026-05-31T10:00:15Z"));
        dto.setAccuracy(31.0);

        locationLogService.filterLocationData(dto);

        verify(locationLogRepository, never()).save(any());
        verifyNoInteractions(parkingRepository, geoService);
    }

    @Test
    void shouldDoNothingWhenLocationIsOutsideRijekaScope() {
        // Test verifies that no save occurs when the location is outside the Rijeka scope.
        LocationLogDTO dto = new LocationLogDTO();
        dto.setLatitude1(45.3271);
        dto.setLongitude1(14.4422);
        dto.setLatitude2(45.3275);
        dto.setLongitude2(14.4430);
        dto.setTimestamp1(Instant.parse("2026-05-31T10:00:10Z"));
        dto.setTimestamp2(Instant.parse("2026-05-31T10:00:15Z"));
        dto.setAccuracy(10.0);

        when(geoService.isInRijekaScope(dto.getLatitude1(), dto.getLongitude1())).thenReturn(false);

        locationLogService.filterLocationData(dto);

        verify(locationLogRepository, never()).save(any());
        verify(parkingRepository, never()).filterAll(any(), any(), any(), any());
    }

    @Test
    void shouldDoNothingWhenTimestamp2EqualsTimestamp1() {
        // Test verifies that no save occurs when timestamp1 equals timestamp2.
        LocationLogDTO dto = createValidDto();
        dto.setTimestamp1(Instant.parse("2026-05-31T10:00:10Z"));
        dto.setTimestamp2(Instant.parse("2026-05-31T10:00:10Z"));

        locationLogService.filterLocationData(dto);

        verify(locationLogRepository, never()).save(any());
        verifyNoInteractions(parkingRepository, geoService);
    }

    @Test
    void shouldDoNothingWhenNoNearbyParkingsExist() {
        // Test verifies that no save occurs when no nearby parkings exist.
        LocationLogDTO dto = createValidDto();

        when(geoService.isInRijekaScope(anyDouble(), anyDouble())).thenReturn(true);
        when(parkingRepository.filterAll(any(), any(), anyDouble(), anyDouble()))
            .thenReturn(List.of());

        locationLogService.filterLocationData(dto);

        verify(locationLogRepository, never()).save(any());
    }

    @Test
    void shouldSaveLocationLogWhenLocationDataIsValid() {
        // Test verifies that a LocationLog is saved when location data is valid and meets conditions.
        LocationLogDTO dto = createValidDto();

        when(geoService.isInRijekaScope(anyDouble(), anyDouble())).thenReturn(true);

        Parking parking = new Parking();
        parking.setLatitude(45.3273);
        parking.setLongitude(14.4428);

        when(parkingRepository.filterAll(any(), any(), anyDouble(), anyDouble()))
                .thenReturn(List.of(parking));

        when(geoService.calculateSpeedKmh(
                dto))
                .thenReturn(10.0);

        when(geoService.calculateHeading(
                dto.getLatitude1(), dto.getLongitude1(),
                dto.getLatitude2(), dto.getLongitude2()))
                .thenReturn(90.0);

        when(geoService.calculateHeading(
                dto.getLatitude2(), dto.getLongitude2(),
                parking.getLatitude(), parking.getLongitude()))
                .thenReturn(90.0);

        when(geoService.calculateDistanceMeters(
                dto.getLatitude2(), dto.getLongitude2(),
                parking.getLatitude(), parking.getLongitude()))
                .thenReturn(20.0);

        when(geoService.calculateDistanceMeters(
                dto.getLatitude1(), dto.getLongitude1(),
                parking.getLatitude(), parking.getLongitude()))
                .thenReturn(25.0);

        ArgumentCaptor<LocationLog> logCaptor = ArgumentCaptor.forClass(LocationLog.class);

        locationLogService.filterLocationData(dto);

        verify(locationLogRepository, times(1)).save(logCaptor.capture());

        LocationLog savedLog = logCaptor.getValue();

        assertEquals(parking, savedLog.getParking());
        assertNotNull(savedLog.getParkingLikelihood());
        assertNotNull(savedLog.getCategory());
        assertEquals(10.0, savedLog.getSpeedKmh());
        assertEquals(90.0, savedLog.getHeadingDegrees());
        assertEquals(dto.getTimestamp2(), savedLog.getEventTimestamp());
    }

    @Test
void shouldAssignStationaryNearCategory() {
        // Test verifies that STATIONARY_NEAR category is assigned for the corresponding scenario.
        LocationLogDTO dto = createValidDto();
    Parking parking = createParking();

    mockCommonValidFlow(dto, parking, 2.0, 20.0, 10.0, 90.0, 90.0);

    ArgumentCaptor<LocationLog> captor = ArgumentCaptor.forClass(LocationLog.class);

    locationLogService.filterLocationData(dto);

    verify(locationLogRepository).save(captor.capture());
    assertEquals(ParkingMovementCategory.STATIONARY_NEAR, captor.getValue().getCategory());
}

    @Test
    void shouldAssignSlowMovingNearCategory() {
        // Test verifies that SLOW_MOVING_NEAR category is assigned for the corresponding scenario.
        LocationLogDTO dto = createValidDto();
        Parking parking = createParking();

        mockCommonValidFlow(dto, parking, 10.0, 50.0, 30.0, 90.0, 90.0);

        ArgumentCaptor<LocationLog> captor = ArgumentCaptor.forClass(LocationLog.class);

        locationLogService.filterLocationData(dto);

        verify(locationLogRepository).save(captor.capture());
        assertEquals(ParkingMovementCategory.SLOW_MOVING_NEAR, captor.getValue().getCategory());
    }

    @Test
    void shouldAssignLeavingAreaCategory() {
        // Test verifies that LEAVING_AREA category is assigned for the corresponding scenario.
        LocationLogDTO dto = createValidDto();
        Parking parking = createParking();

        mockCommonValidFlow(dto, parking, 12.0, 20.0, 80.0, 90.0, 90.0);

        ArgumentCaptor<LocationLog> captor = ArgumentCaptor.forClass(LocationLog.class);

        locationLogService.filterLocationData(dto);

        verify(locationLogRepository).save(captor.capture());
        assertEquals(ParkingMovementCategory.LEAVING_AREA, captor.getValue().getCategory());
    }

    @Test
    void shouldAssignApproachingCategory() {
        // Test verifies that APPROACHING category is assigned for the corresponding scenario.
        LocationLogDTO dto = createValidDto();
        Parking parking = createParking();

        mockCommonValidFlow(dto, parking, 20.0, 120.0, 60.0, 90.0, 90.0);

        ArgumentCaptor<LocationLog> captor = ArgumentCaptor.forClass(LocationLog.class);

        locationLogService.filterLocationData(dto);

        verify(locationLogRepository).save(captor.capture());
        assertEquals(ParkingMovementCategory.APPROACHING, captor.getValue().getCategory());
    }

    @Test
    void shouldAssignPassingByCategory() {
        // Test verifies that PASSING_BY category is assigned for the corresponding scenario.
        LocationLogDTO dto = createValidDto();
        Parking parking = createParking();

        mockCommonValidFlow(dto, parking, 35.0, 100.0, 90.0, 90.0, 90.0);

        ArgumentCaptor<LocationLog> captor = ArgumentCaptor.forClass(LocationLog.class);

        locationLogService.filterLocationData(dto);

        verify(locationLogRepository).save(captor.capture());
        assertEquals(ParkingMovementCategory.PASSING_BY, captor.getValue().getCategory());
    }
}
