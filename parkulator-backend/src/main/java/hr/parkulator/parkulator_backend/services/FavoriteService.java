package hr.parkulator.parkulator_backend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import hr.parkulator.parkulator_backend.entities.Favorite;
import hr.parkulator.parkulator_backend.entities.User;
import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.repositories.FavoriteRepository;
import hr.parkulator.parkulator_backend.repositories.UserRepository;
import hr.parkulator.parkulator_backend.repositories.ParkingRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ParkingRepository parkingRepository;

    public Favorite addFavorite(Long userId, Long parkingId) {

        // 1. provjeri postoji li već
        if (favoriteRepository.findByUserIdAndParkingId(userId, parkingId).isPresent()) {
            throw new RuntimeException("Already in favorites");
        }

        // 2. dohvati usera
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. dohvati parking
        Parking parking = parkingRepository.findById(parkingId)
                .orElseThrow(() -> new RuntimeException("Parking not found"));

        // 4. napravi favorite
        Favorite favorite = Favorite.builder()
                .user(user)
                .parking(parking)
                .build();

        // 5. spremi u bazu
        return favoriteRepository.save(favorite);
    }

    public void removeFavorite(Long userId, Long parkingId) {
        favoriteRepository.deleteByUserIdAndParkingId(userId, parkingId);
        //eventualno dodati provjeru da li uopce postoji
    }

    public List<Favorite> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }
    private FavoriteDTO mapToDTO(Favorite favorite) {
    Parking parking = favorite.getParking();

    return FavoriteDTO.builder()
            .parkingId(parking.getId())
            .name(parking.getName())
            .address(parking.getAddress())
            .type(parking.getType())
            .build();
}
}