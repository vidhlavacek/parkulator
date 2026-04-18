package hr.parkulator.parkulator_backend.dto.parking;

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
    private double Price;
    private int openingHour;
    private int closingHour;
}
