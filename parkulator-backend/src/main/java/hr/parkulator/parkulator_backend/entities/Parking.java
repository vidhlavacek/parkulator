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
    private Double price;
    private Double latitude;
    private Double longitude;
    private String openingHour;
    private String closingHour;
    private Long availableSpots;
    private Long spots;
}
