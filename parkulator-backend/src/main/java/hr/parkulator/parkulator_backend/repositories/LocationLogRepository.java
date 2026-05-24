package hr.parkulator.parkulator_backend.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import hr.parkulator.parkulator_backend.entities.LocationLog;

@Repository
public interface LocationLogRepository extends JpaRepository<LocationLog, Long> {
}
