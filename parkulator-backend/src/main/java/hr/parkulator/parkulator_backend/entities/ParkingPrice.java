package hr.parkulator.parkulator_backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import hr.parkulator.parkulator_backend.shared.WorkDayEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private WorkDayEnum day;
    private String special;
    private int openingHour;
    private int closingHour;
    private double price;

    @JsonIgnore //To avoid possible infinite recursions when called
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parking_id", nullable = false)
    private Parking parking;
}
