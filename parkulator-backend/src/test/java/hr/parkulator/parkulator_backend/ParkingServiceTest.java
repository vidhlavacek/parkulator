package hr.parkulator.parkulator_backend;

import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.repositories.ParkingRepository;
import hr.parkulator.parkulator_backend.services.ParkingService;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class ParkingServiceUnitTest {

    @Test
    void testGetAllParkings() {
        ParkingRepository mockRepo = mock(ParkingRepository.class);
        ParkingService service = new ParkingService(mockRepo);

        Parking p = new Parking(1L, "Parking", "Address", "Garage", 1.0, 2.0, "08:00", "22:00", 50L, 100L);
        when(mockRepo.findAll()).thenReturn(List.of(p));

        List<Parking> result = service.getAllParkings();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Parking");

        verify(mockRepo, times(1)).findAll();
    }
}