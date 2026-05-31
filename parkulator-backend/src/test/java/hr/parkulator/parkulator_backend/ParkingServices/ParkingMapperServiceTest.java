package hr.parkulator.parkulator_backend.ParkingServices;

import hr.parkulator.parkulator_backend.dto.parking.ParkingDTO;
import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.entities.ParkingPrice;
import hr.parkulator.parkulator_backend.services.ParkingServices.ParkingMapperService;
import hr.parkulator.parkulator_backend.shared.WorkDayEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ParkingMapperServiceTest {

    @Autowired
    private ParkingMapperService mapper;

    @Test
    void shouldReturnCorrectPriceWhenInRange() {

        Parking parking = createParking();

        LocalTime fixedTime = LocalTime.of(10, 0);

        ParkingDTO dto = mapper.mapToDTO(
                parking,
                fixedTime,
                WorkDayEnum.WORKDAY
        );

        assertNotNull(dto);
        assertEquals(2.0, dto.getPrice());
        assertNull(dto.getParkingStatus());
    }

    @Test
    void shouldReturnZeroWhenOutOfRange() {

        Parking parking = createParking();

        LocalTime fixedTime = LocalTime.of(23, 0);

        ParkingDTO dto = mapper.mapToDTO(
                parking,
                fixedTime,
                WorkDayEnum.WORKDAY
        );

        assertNotNull(dto);
        assertEquals(0.0, dto.getPrice());
        assertNotNull(dto.getParkingStatus());
    }

    @Test
    void shouldThrowWhenCoordinatesMissing() {

        Parking parking = createParking();
        parking.setLatitude(null);

        LocalTime fixedTime = LocalTime.of(10, 0);

        assertThrows(RuntimeException.class, () ->
                mapper.mapToDTO(parking, fixedTime, WorkDayEnum.WORKDAY)
        );
    }

    private Parking createParking() {

        Parking parking = new Parking();
        parking.setId(1L);
        parking.setName("Test Parking");
        parking.setLatitude(45.0);
        parking.setLongitude(15.0);

        ParkingPrice price = new ParkingPrice();
        price.setPrice(2.0);
        price.setOpeningHour(8);
        price.setClosingHour(20);
        price.setDay(WorkDayEnum.WORKDAY);

        parking.setParkingPrices(List.of(price));

        return parking;
    }
}