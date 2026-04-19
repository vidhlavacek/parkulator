package hr.parkulator.parkulator_backend.dto.parking;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ParkingDataDTO {
    private String sourceKey;
    private String name;
    private String address;
    private String link;
    private String type;
    private boolean isLive;
    private Long spots;
    private Long availableSpots;
    List<ParkingPriceDTO> parkingPrice;
}
