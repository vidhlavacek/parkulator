package hr.parkulator.parkulator_backend.ParkingServices;

import hr.parkulator.parkulator_backend.dto.parking.ParkingSearchResponseDTO;
import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.exception.BadRequestException;
import hr.parkulator.parkulator_backend.exception.NoParkingsFoundException;
import hr.parkulator.parkulator_backend.repositories.ParkingRepository;
import hr.parkulator.parkulator_backend.services.ParkingServices.ParkingMapperService;
import hr.parkulator.parkulator_backend.services.ParkingServices.ParkingScoreService;
import hr.parkulator.parkulator_backend.services.ParkingServices.ParkingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    private ParkingRepository parkingRepository;

    @Mock
    private ParkingMapperService parkingMapperService;

    @Mock
    private ParkingScoreService parkingScoreService;

    private ParkingService parkingService;

    @BeforeEach
    void setUp() {
        parkingService = new ParkingService(
                parkingRepository,
                parkingMapperService,
                parkingScoreService
        );
    }

    @Test
    void returnResults() {

        Parking parking = createParking();

        when(parkingRepository.findAll())
                .thenReturn(List.of(parking));

        when(parkingMapperService.mapToDTO(any()))
                .thenAnswer(invocation -> {
                    Parking p = invocation.getArgument(0);

                    var dto = new hr.parkulator.parkulator_backend.dto.parking.ParkingDTO();
                    dto.setLatitude(p.getLatitude());
                    dto.setLongitude(p.getLongitude());
                    dto.setPrice(10.0);
                    dto.setType(p.getType());

                    return dto;
                });

        when(parkingScoreService.score(anyList(), anyDouble(), anyDouble()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ParkingSearchResponseDTO result = parkingService.getFilteredParkings(
                null,
                null,
                null,
                45.0,
                15.0
        );

        assertNotNull(result);
        assertNotNull(result.getParkings());
    }

    @Test
    void exceptionWhenCoordinatesAreNull() {
        assertThrows(BadRequestException.class, () ->
                parkingService.getFilteredParkings(
                        null, null, null, null, null
                )
        );
    }

    @Test
    void exceptionWhenNegativePrice() {
        assertThrows(BadRequestException.class, () ->
                parkingService.getFilteredParkings(
                        null, -5.0, null, 45.0, 15.0
                )
        );
    }

    @Test
    void exceptionWhenNegativeDistance() {
        assertThrows(BadRequestException.class, () ->
                parkingService.getFilteredParkings(
                        null, null, -1.0, 45.0, 15.0
                )
        );
    }

    @Test
    void exceptionWhenNoParkingsFound() {

        when(parkingRepository.filterAll(any(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of());

        assertThrows(NoParkingsFoundException.class, () ->
                parkingService.getFilteredParkings(
                        "NON_EXISTING_TYPE",
                        0.01,
                        0.01,
                        45.0,
                        15.0
                )
        );
    }

    @Test
    void radiusExpandsWhenNoResultsFoundInitially() {
        Parking parking = createParking();

        when(parkingRepository.filterAll(any(), anyDouble(), anyDouble(), anyDouble()))
                .thenAnswer(invocation -> {
                    Double radius = invocation.getArgument(1);
                    return radius < 0.1 ? List.of() : List.of(parking);
                });

        when(parkingMapperService.mapToDTO(any()))
                .thenAnswer(invocation -> {
                    Parking p = invocation.getArgument(0);
                    var dto = new hr.parkulator.parkulator_backend.dto.parking.ParkingDTO();
                    dto.setLatitude(p.getLatitude());
                    dto.setLongitude(p.getLongitude());
                    dto.setPrice(10.0);
                    dto.setType(p.getType());
                    return dto;
                });

        when(parkingScoreService.score(anyList(), anyDouble(), anyDouble()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ParkingSearchResponseDTO result = parkingService.getFilteredParkings(
                null,
                null,
                0.01,
                45.0,
                15.0
        );

        assertNotNull(result);
        assertTrue(result.isRadiusExpanded());
        assertEquals(0.12, result.getFinalRadius());
        assertFalse(result.getParkings().isEmpty());
    }

    @Test
    void filterByMaxPrice() {

        Parking parking = createParking();

        when(parkingRepository.findAll())
                .thenReturn(List.of(parking));

        when(parkingMapperService.mapToDTO(any()))
                .thenAnswer(invocation -> {
                    var dto = new hr.parkulator.parkulator_backend.dto.parking.ParkingDTO();
                    dto.setPrice(3.0);
                    return dto;
                });

        when(parkingScoreService.score(anyList(), anyDouble(), anyDouble()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ParkingSearchResponseDTO result = parkingService.getFilteredParkings(
                null, 5.0, null, 45.0, 15.0
        );

        result.getParkings().forEach(p ->
                assertTrue(p.getPrice() <= 5.0)
        );
    }

    private Parking createParking() {
        Parking p = new Parking();
        p.setId(1L);
        p.setName("Test Parking");
        p.setLatitude(45.0);
        p.setLongitude(15.0);
        p.setType("Otvorena parkirališta");
        return p;
    }
}
