package hr.parkulator.parkulator_backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LiveParkingDataDTO {
    private Long externalId;
    private String name;
    private Double price;
    private String address;
    private String link;
    private boolean isLive;
    private Long spots;
    private Long availableSpots;
    List<WorkHoursDTO> workHours;
}
