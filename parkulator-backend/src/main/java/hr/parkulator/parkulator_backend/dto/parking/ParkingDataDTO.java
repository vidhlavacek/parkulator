package hr.parkulator.parkulator_backend.dto.parking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ParkingDataDTO {
    private String sourceKey;
    private String name;
    private String address;
    private String link;
    private String type;
    private boolean isLive;
    private Long spots;
    private Long availableSpots;
    private Double latitude;
    private Double longitude;
    List<ParkingPriceDTO> parkingPrice;
}