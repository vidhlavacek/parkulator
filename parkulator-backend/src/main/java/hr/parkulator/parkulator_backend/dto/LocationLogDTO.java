package hr.parkulator.parkulator_backend.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationLogDTO {
    private Double latitude1;
    private Double longitude1;
    private Instant timestamp1;
    private Double latitude2;
    private Double longitude2;
    private Instant timestamp2;
    private Double accuracy;
}
