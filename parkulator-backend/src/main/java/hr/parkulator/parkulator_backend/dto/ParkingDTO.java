package hr.parkulator.parkulator_backend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingDTO {
    private String name;
    private String address;
    private String type;
    private Double price;
    private String openingHour;
    private String closingHour;
}
