package hr.parkulator.parkulator_backend.ParkingServices;

import hr.parkulator.parkulator_backend.dto.parking.ParkingDTO;
import hr.parkulator.parkulator_backend.shared.ParkingOccupancyCategory;
import hr.parkulator.parkulator_backend.services.ParkingServices.ParkingScoreService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParkingScoreServiceTest {
    private final ParkingScoreService parkingScoreService = new ParkingScoreService();

    // Business logic:
    @Test
    void sortByScoreDescending(){
        ParkingDTO p1 = createParking(45.0, 15.0, 10.0, 10L, 20L);
        ParkingDTO p2 = createParking(45.1, 15.1, 2.0, 1L, 20L);

        List<ParkingDTO> result = parkingScoreService.score(
                List.of(p1, p2),
                45.0,
                15.0
        );

        assertEquals(2, result.size());
        assertTrue(result.get(0).getScore() >= result.get(1).getScore());
    }

    @Test
    void ignorePriceWhenMessageExists(){
        ParkingDTO p = createParking(45.0, 15.0, 1000.0, 10L, 20L);
        p.setParkingStatus("null");

        List<ParkingDTO> result = parkingScoreService.score(
                List.of(p),
                45.0,
                15.0
        );

        assertNotNull(result.get(0).getScore());
    }

    @Test
    void calculateScoreBasedOnDistance(){
        ParkingDTO p1 = createParking(45.0, 15.0, 5.0, 10L, 20L);
        ParkingDTO p2 = createParking(46.0, 16.0, 5.0, 10L, 20L);

        List<ParkingDTO> result = parkingScoreService.score(
                List.of(p1, p2),
                45.0,
                15.0
        );

        assertEquals(2, result.size());
        assertNotNull(result.get(0).getScore());
        assertNotNull(result.get(1).getScore());
    }


    // Edge case:
    @Test
    void emptyListWhenInputIsEmpty(){
        List<ParkingDTO> result = parkingScoreService.score(
                List.of(),
                45.0,
                15.0
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void availabilityForLiveParking(){
        ParkingDTO p = createParking(45.0, 15.0, 5.0, 10L, 20L);
        p.setLive(true);

        List<ParkingDTO> result = parkingScoreService.score(
                List.of(p),
                45.0,
                15.0
        );

        assertNotNull(result.get(0).getScore());
    }

    @Test
    void useEstimatedAvailabilityWhenParkingIsOffline(){
        ParkingDTO p = createParking(45.0, 15.0, 5.0, 0L, 0L);
        p.setLive(false);
        p.setOccupancyStatus(ParkingOccupancyCategory.MODERATELY_OCCUPIED);

        List<ParkingDTO> result = parkingScoreService.score(
                List.of(p),
                45.0,
                15.0
        );

        assertNotNull(result.get(0).getScore());
    }

    @Test
    void returnSameSizeList(){
        ParkingDTO p1 = createParking(45.0, 15.0, 10.0, 10L, 20L);
        ParkingDTO p2 = createParking(45.1, 15.1, 2.0, 5L, 10L);
        ParkingDTO p3 = createParking(45.2, 15.2, 5.0, 8L, 12L);

        List<ParkingDTO> result = parkingScoreService.score(
                List.of(p1, p2, p3),
                45.0,
                15.0
        );

        assertEquals(3, result.size());
    }


    // Data builder:
    private ParkingDTO createParking(
            Double lat,
            Double lng,
            Double price,
            Long available,
            Long spots
    ) {
        ParkingDTO p = new ParkingDTO();
        p.setLatitude(lat);
        p.setLongitude(lng);
        p.setPrice(price);
        p.setAvailableSpots(available);
        p.setSpots(spots);
        p.setLive(true);
        p.setParkingStatus(null);
        return p;
    }
}
