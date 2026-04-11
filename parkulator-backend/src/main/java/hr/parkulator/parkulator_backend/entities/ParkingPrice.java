package hr.parkulator.parkulator_backend.entities;

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
    private long id;

    @Enumerated(EnumType.STRING)
    private WorkDayEnum day;
    private String special;
    private int openingHour;
    private int closingHour;
    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_id")
    private Parking parking;
}
