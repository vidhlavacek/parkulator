package hr.parkulator.parkulator_backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Parking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceKey;
    private String name;
    private String address;
    private String link;
    private String type;
    private boolean isLive;
    //private Double latitude;
    //private Double longitude;
    private Long spots;
    private Long availableSpots;

    @OneToMany(mappedBy = "parking", cascade = CascadeType.ALL, orphanRemoval = true)

    @Builder.Default
    List<ParkingPrice> parkingPrices = new ArrayList<>();

    public void addPrice(ParkingPrice price) {
        parkingPrices.add(price);
        price.setParking(this);
    }
    public void removePrice(ParkingPrice price) {
        parkingPrices.remove(price);
        price.setParking(null);
    }
}
