package hr.parkulator.parkulator_backend.dto.parking;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"radiusExpanded", "finalRadius", "parkings"})
public class ParkingSearchResponseDTO {
    private boolean radiusExpanded;
    private Double finalRadius;
    private List<ParkingDTO> parkings;
}
