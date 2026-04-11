package hr.parkulator.parkulator_backend.repository;

import hr.parkulator.parkulator_backend.model.Parking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingRepository extends JpaRepository<Parking, Long> {
}