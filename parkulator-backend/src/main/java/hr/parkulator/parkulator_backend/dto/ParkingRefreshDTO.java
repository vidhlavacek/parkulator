package hr.parkulator.parkulator_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ParkingRefreshDTO {
    private Long externalID;
    private boolean isLive;
    private Long availableSpots;
    private List<ParkingPriceDTO> price;
}
