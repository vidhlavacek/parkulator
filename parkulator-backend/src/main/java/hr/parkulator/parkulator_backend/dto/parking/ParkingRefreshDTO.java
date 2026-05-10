package hr.parkulator.parkulator_backend.dto.parking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ParkingRefreshDTO {
    private String sourceKey;
    private String name;
    private boolean isLive;
    private Long availableSpots;
    private List<ParkingPriceDTO> parkingPrice;
}
