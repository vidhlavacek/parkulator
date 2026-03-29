package hr.parkulator.parkulator_backend.repositories;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hr.parkulator.parkulator_backend.entities.Parking;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, Long> {

    Optional<Parking> findByName(String name);

    Optional<Parking> findByAddress(String address);

    List<Parking> findByType(String type);

    //List<Parking> findByPriceRange(Double lowestPrice, Double highestPrice);

    //List<Parking> findByTypeAndPrice(String type, Double lowestPrice, Double highestPrice);

}
