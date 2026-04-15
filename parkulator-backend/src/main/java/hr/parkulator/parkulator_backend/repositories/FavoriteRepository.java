package hr.parkulator.parkulator_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hr.parkulator.parkulator_backend.entities.Favorite;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(Long userId);

    Optional<Favorite> findByUserIdAndParkingId(Long userId, Long parkingId);

    void deleteByUserIdAndParkingId(Long userId, Long parkingId);
}