package hr.parkulator.parkulator_backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Parking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String type;
    private Double lowestPrice;
    private Double highestPrice;
    private String openingHour;
    private String closingHour;
    private Long availableSpots;
    private Long spots;
}
