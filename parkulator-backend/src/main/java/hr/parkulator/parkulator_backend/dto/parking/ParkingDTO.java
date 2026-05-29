package hr.parkulator.parkulator_backend.dto.parking;

import hr.parkulator.parkulator_backend.shared.ParkingOccupancyCategory;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingDTO {
    private String name;
    private String address;
    private String type;
    private String link;
    private boolean isLive;
    private Long availableSpots;
    private Long spots;
    private String parkingStatus;
    private double Price;
    private int openingHour;
    private int closingHour;
    private Double latitude;
    private Double longitude;
    private Double score;
    private ParkingOccupancyCategory occupancyStatus;}
