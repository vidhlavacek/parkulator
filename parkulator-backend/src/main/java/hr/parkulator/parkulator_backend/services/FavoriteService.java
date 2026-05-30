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
import java.util.stream.Collectors;
import hr.parkulator.parkulator_backend.dto.parking.ParkingDTO;

@Service
@AllArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ParkingRepository parkingRepository;

    public Favorite addFavorite(Long userId, Long parkingId) {

        
        if (favoriteRepository.findByUserIdAndParkingId(userId, parkingId).isPresent()) {
            throw new RuntimeException("Already in favorites");
        }

     
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

       
        Parking parking = parkingRepository.findById(parkingId)
                .orElseThrow(() -> new RuntimeException("Parking not found"));

     
        Favorite favorite = Favorite.builder()
                .user(user)
                .parking(parking)
                .build();

        return favoriteRepository.save(favorite);
    }

    public void removeFavorite(Long userId, Long parkingId) {
        favoriteRepository.deleteByUserIdAndParkingId(userId, parkingId);
       
    }

    public List<ParkingDTO> getUserFavorites(Long userId) {
    return favoriteRepository.findByUserId(userId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
}
    private ParkingDTO mapToDTO(Favorite favorite) {
    Parking parking = favorite.getParking();

    return ParkingDTO.builder()
        .id(parking.getId())
        .name(parking.getName())
        .address(parking.getAddress())
        .type(parking.getType())
        .link(parking.getLink())
        .isLive(parking.isLive())
        .availableSpots(parking.getAvailableSpots())
        .latitude(parking.getLatitude())
        .longitude(parking.getLongitude())
        .build();
}
}