package hr.parkulator.parkulator_backend.repositories;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import hr.parkulator.parkulator_backend.entities.Parking;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, Long> {

    Optional<Parking> findByName(String name);

    Optional<Parking> findByAddress(String address);

    List<Parking> findByType(String type);

    @Query(value = """
        SELECT *
        FROM parking p
        WHERE (:type IS NULL OR LOWER(p.type) = LOWER(:type))
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (
            :maxDistance IS NULL OR :lat IS NULL OR :lng IS NULL
            OR (
                6371 * acos(
                    cos(radians(:lat)) *
                    cos(radians(p.latitude)) *
                    cos(radians(p.longitude) - radians(:lng)) +
                    sin(radians(:lat)) *
                    sin(radians(p.latitude))
                )
            ) <= :maxDistance
        )
    """, nativeQuery = true)
    List<Parking> filterAll(
            @Param("type") String type,
            @Param("maxPrice") Double maxPrice,
            @Param("maxDistance") Double maxDistance,
            @Param("lat") Double lat,
            @Param("lng") Double lng
    );

}
