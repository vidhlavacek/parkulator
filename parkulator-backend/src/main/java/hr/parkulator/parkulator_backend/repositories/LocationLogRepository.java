package hr.parkulator.parkulator_backend.repositories;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import hr.parkulator.parkulator_backend.entities.LocationLog;

@Repository
public interface LocationLogRepository extends JpaRepository<LocationLog, Long> {
    List<LocationLog> findByEventTimestampBetweenOrderByEventTimestampAsc(Instant start, Instant end);
}