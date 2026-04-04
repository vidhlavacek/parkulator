package hr.parkulator.parkulator_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LiveParkingRefreshDTO {
    private Long externalID;
    private Double price;
    private boolean isLive;
    private Long availableSpots;
}
