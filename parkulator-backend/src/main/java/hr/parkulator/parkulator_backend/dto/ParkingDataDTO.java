package hr.parkulator.parkulator_backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ParkingDataDTO {
    private Long externalId;
    private String name;
    private String address;
    private String link;
    private boolean isLive;
    private Long spots;
    private Long availableSpots;
    List<ParkingPriceDTO> parkingPrice;
}
