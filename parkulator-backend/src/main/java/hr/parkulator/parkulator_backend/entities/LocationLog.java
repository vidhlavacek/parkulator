package hr.parkulator.parkulator_backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

import hr.parkulator.parkulator_backend.shared.ParkingMovementCategory;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class LocationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parking_id", nullable = false)
    private Parking parking;

    private Double parkingLikelihood;
    
    @NotNull
    private Double speedKmh;
    private Double headingDegrees;
    private Double distanceToParkingMeters;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private ParkingMovementCategory category;
    
    @NotNull
    private Instant eventTimestamp;
}
